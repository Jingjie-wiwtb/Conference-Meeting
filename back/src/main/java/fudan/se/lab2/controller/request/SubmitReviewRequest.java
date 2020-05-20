package fudan.se.lab2.controller.request;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SubmitReviewRequest {

    @NotNull(message = "论文id不能为null")
    @Min(value = 1, message = "论文id至少为1")
    private Long paper_id;

    @NotNull(message = "score不能为null")
    @Range(min = -2, max = 2, message = "score至少为1")
    private int score;

    @NotBlank(message = "comment不能为空")
    private String comment;

    @NotNull(message = "confidence不能为null")
    @Range(min = 0, max = 3, message = "confidence至少为1")
    private int confidence;

    //需要默认的构造函数，否则无法反序列化
    public SubmitReviewRequest() {}

    public SubmitReviewRequest(Long paper_id, int score, String comment, int confidence) {
        this.paper_id = paper_id;
        this.score = score;
        this.comment = comment;
        this.confidence = confidence;
    }

    public int getScore() {
        return score;
    }

    public Long getPaper_id() {
        return paper_id;
    }

    public int getConfidence() {
        return confidence;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPaper_id(Long paper_id) {
        this.paper_id = paper_id;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
