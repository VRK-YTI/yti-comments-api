package fi.vm.yti.comments.api.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "commentroundgroup")
@XmlRootElement
public class CommentRoundGroup extends AbstractIdentifyableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommentRound commentRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentround_id", nullable = false)
    public CommentRound getCommentRound() {
        return commentRound;
    }

    public void setCommentRound(final CommentRound commentRound) {
        this.commentRound = commentRound;
    }
}
