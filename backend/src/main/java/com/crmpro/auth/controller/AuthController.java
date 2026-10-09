package com.crmpro.auth.controller;

import com.crmpro.auth.dto.LoginRequest;
import com.crmpro.auth.dto.LoginResponse;
import com.crmpro.auth.dto.RefreshTokenRequest;
import com.crmpro.auth.dto.RegisterCompanyRequest;
import com.crmpro.auth.service.AuthService;
import com.crmpro.common.response.ApiResponse;
import com.crmpro.security.UserPrincipal;
import com.crmpro.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro de empresa, login e renovação de token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register-company")
    @Operation(summary = "Registrar nova empresa e usuário administrador")
    public ResponseEntity<ApiResponse<LoginResponse>> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        LoginResponse response = authService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Empresa e administrador cadastrados com sucesso", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e emitir tokens JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login realizado com sucesso", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acesso usando refresh token")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token renovado com sucesso", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal principal) {
        UserResponse user = authService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
