import api from '../../../api/api';

export const AuthAPI = {
  register: (fullName, userName, password, email) =>
    api.post('/users/public/register', { fullName, userName, password, email }),
  login: (userName, password) =>
    api.post('/auth/login', { userName, password }),
  passwordRecoveryRequest: ({ userName, email }) =>
    api.post('email/public/password-recovery-request', { userName, email }),
};