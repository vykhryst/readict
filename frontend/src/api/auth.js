import api from './axios';

export const register = ({email, firstName, lastName, password, favouriteGenreIds}) =>
    api.post('/auth/register', {
        email,
        firstName,
        lastName,
        password,
        favouriteGenreIds
    });

export const login = (email, password) =>
    api.post("/auth/login", {email, password});

export const logout = () =>
    api.post("/auth/logout");

export const me = () =>
    api.get("/user/me");      // 200 + UserDto або 204 No Content