package gr.grnet.eseal.exception;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * APIError encapsulates all needed information that should return as a response to the API client
 * when an error has occurred.
 */
@Getter
@Setter
@NoArgsConstructor
public class APIError {

    @JsonProperty("error")
    private APIErrorBody apiErrorBody;

    APIError(int code, String message, HttpStatus status) {
        this.apiErrorBody =  new APIErrorBody(code, message, status);
    }

    @Getter
    @Setter
    public class APIErrorBody {
        private int code;
        private String message;
        private HttpStatus status;

        private APIErrorBody() {
        }

        private APIErrorBody(int code, String message, HttpStatus status) {
            this.code = code;
            this.message = message;
            this.status = status;
        }
    }
}
