// src/api/series.js
import api from "./axios";

export const fetchSeriesById = (id) => api.get(`/series/${id}`);

export const fetchSeriesStats = (id) => api.get(`/series/${id}/stats`);

export const fetchSeriesBooks = (id, {page = 0, size = 12, sort = "seriesNumber,asc"}) =>
    api.get(`/series/${id}/books`, {
        params: {page, size, sort},
    });