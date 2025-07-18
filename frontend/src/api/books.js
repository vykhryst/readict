import api from "./axios";

export const fetchGenres = () => api.get("/genres", {params: {size: 1000}});

export const fetchBooks = ({title, genreIds, page, size, sort}) =>
    api.get("/books", {
        params: {
            title: title || undefined,
            genreIds: genreIds.length ? genreIds.join(",") : undefined,
            page,
            size,
            sort,
        },
    });

export const fetchBookById = id => api.get(`/books/${id}`);