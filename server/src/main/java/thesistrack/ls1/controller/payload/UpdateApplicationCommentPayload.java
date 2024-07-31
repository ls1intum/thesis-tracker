package thesistrack.ls1.controller.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationCommentPayload implements Serializable {
    private String comment;
}