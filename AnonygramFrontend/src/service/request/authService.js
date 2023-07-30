import request from ".";

const login = (email, password) => request.postMethod(
  "/auth/login", 
  {email, password}
);

const anonymous = () => request.postMethod(
  "/auth/anonymous"
);

const register = (email, userName, password) => request.postMethod(
  "/auth/register", 
  { email, userName, password }
);

const ssl = () => request.getMethod(
  "/ssl"
);

export default {
  login,
  anony,
  register,
  ssl
};