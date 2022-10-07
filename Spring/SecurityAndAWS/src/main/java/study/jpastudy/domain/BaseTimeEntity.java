package study.jpastudy.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass       // Entity 클래스들이 이 클래스를 상속할 경우 필드들도 칼럼으로 인식
@Getter
@EntityListeners(AuditingEntityListener.class)      // 이 클래스에 Auditing 기능 포함
public abstract class BaseTimeEntity {

    @CreatedDate        // Entity가 생성되어 저장될 때 시간이 자동 저장
    private LocalDateTime createdDate;

    @LastModifiedBy     // 조회한 Entity의 값을 변경시 시간 자동 저장
    private LocalDateTime modifiedDate;
}
