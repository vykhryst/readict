import api from "./axios";

export const getMe = () =>
    api.get("/user/me")

export const updateMe = body => api.put("/user/me", body);
export const changePwd = body => api.put("/user/me/password", body);
