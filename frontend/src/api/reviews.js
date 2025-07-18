import api from "./axios";

export const getReviews = (bookId, page = 0) =>
    api.get(`/books/${bookId}/reviews`, {params: {page, size: 5}});

export const getMyReview = (bookId) =>
    api.get(`/books/${bookId}/reviews/me`);

export const postReview = (bookId, content) =>
    api.post(`/books/${bookId}/reviews`, {content});

export const deleteReview = (bookId) =>
    api.delete(`/books/${bookId}/reviews`);

