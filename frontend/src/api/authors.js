import api from "./axios";

export const fetchAuthorById = (id) => api.get(`/authors/${id}`);

export const fetchAuthorStats = (id) => api.get(`/authors/${id}/stats`);

export const fetchAuthorBooks = (id, {
    page = 0,
    size = 8,
    sort = "seriesNumber,asc"
}) =>
    api.get(`/authors/${id}/books`, {
        params: {
            page,
            size,
            sort,
        },
    });