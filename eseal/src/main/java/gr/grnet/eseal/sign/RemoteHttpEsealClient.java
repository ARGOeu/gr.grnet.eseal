package gr.grnet.eseal.sign;

import static net.logstash.logback.argument.StructuredArguments.f;

import gr.grnet.eseal.logging.BackEndLogField;
import gr.grnet.eseal.sign.request.AbstractRemoteProviderRequest;
import gr.grnet.eseal.sign.response.AbstractRemoteProviderResponse;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * Interface that represents clients that can access remote qualified e-seals and use them to
 * executeRemoteProviderRequestResponse the content in {@link Request}
 */
public interface RemoteHttpEsealClient<
    Request extends AbstractRemoteProviderRequest,
    Response extends AbstractRemoteProviderResponse> {

  Response executeRemoteProviderRequestResponse(
      Request request,
      Class<Response> clazz,
      BiFunction<BackEndLogField, Logger, Supplier<Predicate<AbstractRemoteProviderResponse>>>
          errorResponseFunction);

  static Predicate<AbstractRemoteProviderResponse> errorResponsePredicate(
      String message,
      BackEndLogField field,
      Predicate<? super AbstractRemoteProviderResponse> predicate,
      RuntimeException exc,
      Logger logger) {
    return t -> {
      boolean r = predicate.test(t);
      if (r) {
        logger.error(message, f(field));
        throw exc;
      }
      return r;
    };
  }
}
