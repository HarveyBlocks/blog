```java
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler)
                .and()
```

```java
@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, 
                         HttpServletResponse response, 
                         AuthenticationException authException) 
        throws IOException, ServletException {
        response.getWriter().write("login failed:" + authException.getMessage());
    }
}
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, 
                       HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) 
        throws IOException, ServletException {
        response.setStatus(403);
        response.getWriter().write("Forbidden:" + accessDeniedException.getMessage());
    }
}

```

