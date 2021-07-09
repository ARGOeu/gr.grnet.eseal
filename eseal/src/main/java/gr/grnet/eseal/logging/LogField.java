package gr.grnet.eseal.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import gr.grnet.eseal.enums.LogType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** The {@link LogField} holds the generic info to be logged. */
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogField {

  private LogType type;
}
