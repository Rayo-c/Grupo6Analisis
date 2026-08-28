export interface LoginRequest {
  correo: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  usuarioId: number;
  nombreCompleto: string;
  correo: string;
  rol: string;
}

// CU02 - Registrar Cliente
export interface RegistroClienteRequest {
  nombreCompleto: string;
  correo: string;
  telefono?: string;
  password: string;
}

// CU03 - Recuperar Contrasena
export interface RecuperarPasswordRequest {
  correo: string;
}

export interface ResetPasswordRequest {
  token: string;
  nuevaPassword: string;
}
