package org.iu.flashcards.api.login;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthorizationFilter implements Filter {
  private final ObjectFactory<UserComponent> userComponentFactory;
  private final UserRepository userRepository;
  private final TokenValidator tokenValidator;

  @Autowired
  public AuthorizationFilter(ObjectFactory<UserComponent> userComponentFactory, UserRepository userRepository, TokenValidator tokenValidator) {
    this.userComponentFactory = userComponentFactory;
    this.userRepository = userRepository;
    this.tokenValidator = tokenValidator;
  }

  @Override
  public void init(FilterConfig filterConfig) {
    // Initialisierung, falls nötig
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      return;
    }

    String path = httpRequest.getRequestURI();
    String authHeader = httpRequest.getHeader("Authorization");

    if ("/login".equals(path) || "/signup".equals(path)) {
      // Skip filter for /login and /signup
      chain.doFilter(request, response);
    } else {
      if (authHeader == null || authHeader.isEmpty()) {
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      } else {
        try {
          var claims = tokenValidator.validate(authHeader);
          var uniqueId = claims.get("uniqueId").toString();
          var user = userRepository.findByUniqueId(uniqueId);
          if (user.isEmpty()) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
          }
          userComponentFactory.getObject().setUser(user.get());
          chain.doFilter(request, response);
        } catch (LoginFailedException tokenInvalid) {
          httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
      }
    }
  }

  @Override
  public void destroy() {
    // Ressourcenfreigabe, falls nötig
  }
}
