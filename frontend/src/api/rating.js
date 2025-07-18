import api from "./axios";

export const getMyRating = (bookId) =>
    api.get(`/books/${bookId}/rating/me`);

export const postRating = (bookId, score) =>
    api.post(`/books/${bookId}/rating`, {score});

export const deleteRating = bookId =>
    api.delete(`/books/${bookId}/rating`);