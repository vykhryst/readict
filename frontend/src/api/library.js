import api from "./axios";

export const getShelf = bookId => api.get(`/library/${bookId}`);

export const setShelf = (bookId, shelf) =>
    api.put(`/library/${bookId}`, null, {params: {shelf}});

export const removeBook = bookId => api.delete(`/library/${bookId}`);

export const getLibrarySummary = () =>
    api.get("/library/summary");          // { total, read, reading, want }


const serialize = params => {
    const qs = new URLSearchParams();
    Object.entries(params).forEach(([k, v]) => {
        if (v == null || v === "" || (Array.isArray(v) && v.length === 0)) return;

        if (Array.isArray(v))
            v.forEach(x => qs.append(k, x));
        else
            qs.append(k, v);
    });
    return qs.toString();
};


export const getLibraryPage = params =>
    api.get("/library", {params, paramsSerializer: serialize});