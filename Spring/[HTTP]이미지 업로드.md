스프링으로 간단히 이미지를 업로드하고 조회하는 기능을 만들어보자.

<br>
<br>

# 구조

## 업로드

- 뷰에서 파일을 업로드하면 서버의 컨트롤러에서 받는다.
- 컨트롤러에서는 수신된 파일을 복사하여 새로운 이름을 부여해서 특정 디렉토리에 저장한다.
- 동시에, db에는 파일의 정보를 담고있는 엔티티를 저장한다 (Files 엔티티. 파일 자체를 저장하지는 않는다!) 

<br>
<br>


## 조회

-  파일들의 정보를 담고있는 `List<Files>`를 `Model`에 담아서 서버사이드 렌더링 한다. (SSR)
- 뷰에서는 `Model`로부터 수신한 리스트를 이용해서 이미지 및 정보를 보여준다.
- 특정 디렉토리에 있는 이미지를 보여주기 위해  `<img th:src="${'/img/'+ file.filename}" style="width:300px;height:auto;">` 를 사용했다.

<br>
<br>


# 코드

## upLoadImage.html

```html
    <form action="/uploadImage" method="post" enctype="multipart/form-data">
        <input type="file" name = "files">
        <!-- 여기서 files는 controller에 @RequestPart MultipartFile files -->
        <button type="submit" class="btn btn-dark">업로드</button>
    </form>
```

- 업로드 버튼을 누르면 `"/uploadImage"` URI로 POST Request를 보낸다.
- Request의 Body에 file을 실어서 보낸다.

<br>
<br>


## Files와 관련된 클래스들

```java
// 엔티티
@Data
@Entity
@Getter @Setter
public class Files {
    @Id
    @GeneratedValue()
    private Long id;

    private String filename;
    private String fileOriName;
    private String fileurl;
}


// DAO
@Repository
@RequiredArgsConstructor
public class FilesRepository {

    private final EntityManager em;

    public void save(Files files) {
        em.persist(files);
    }

    public Files findOne(Long filesId) {
        return em.find(Files.class, filesId);
    }

    public List<Files> findAll() {
        List<Files> result = em.createQuery("select f from Files f", Files.class)
                .getResultList();
        return result;
    }
}


// Service
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;

    @Transactional
    public Long join(Files files) {
        filesRepository.save(files);
        return files.getId();
    }

    public Files findOne(Long filesId) {
        return filesRepository.findOne(filesId);
    }

    public List<Files> findAll() {
        return filesRepository.findAll();
    }
}
```

<br>
<br>



## Controller

```java
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final FilesService filesService;

    // 업로드 화면
    @GetMapping("/images/new")
    public String createImage(Model model) {

        return "imageFile/createImage";
    }

    // 업로드 요청시 실행
    @PostMapping("/uploadImage")
    public String uploadImage(HttpServletRequest request, @RequestPart MultipartFile files) throws Exception{
        Files file = new Files();

        String sourceFileName = files.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        File destinationFile;
        String destinationFileName;
        String fileUrl = "C:\\Users\\didrl\\OneDrive - 홍익대학교\\바탕 화면\\board\\src\\main\\resources\\static\\img\\";

        do {
            destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;
            destinationFile = new File(fileUrl + destinationFileName);
        } while (destinationFile.exists());

        destinationFile.getParentFile().mkdirs();
        files.transferTo(destinationFile);

        file.setFilename(destinationFileName);
        file.setFileOriName(sourceFileName);
        file.setFileurl(fileUrl);
        filesService.join(file);

        return "redirect:/";
    }

    // 이미지들 조회
    @GetMapping("/images")
    public String showIamges(Model model) {

        model.addAttribute("files", filesService.findAll());
        return "imageFile/showImage";
    }
}
```

<br>
<br>


## showImage.html

```html
        <table class="table table-striped">
            <thead>

            <tr>
                <th>이미지 번호</th>
                <th>이름</th>
                <th>이미지</th>
            </tr>

            </thead>
            <tbody>
            <tr th:each="file : ${files}">

                <td th:text="${file.id}"></td>
                <td th:text="${file.fileOriName}"></td>
                <td>
                    <img th:src="${'/img/'+ file.filename}" style="width:300px;height:auto;">
                </td>

            </tr>
            </tbody>
        </table>
```

<br>
<br>


# 참고사항

## defendency 설정

- `RandomStringUtils`  <- `implementation 'org.apache.commons:commons-lang3:3.12.0'`
- `FilenameUtils`  <-  `implementation 'commons-io:commons-io:2.11.0'`

## 화면 캡쳐

- 업로드

![업로드](https://user-images.githubusercontent.com/97036481/183282297-2ee7f2ab-ee51-4f02-8a3a-07aa7791cab1.png)

- 조회

![조회](https://user-images.githubusercontent.com/97036481/183282303-abec6e95-e2e0-4b19-a766-7f13f8520348.png)

