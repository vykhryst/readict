import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true,      // щоб сесія (JSESSIONID) прилипала
    credentials: "include",
    headers: {
        'Content-Type': 'application/json'
    }
});

export default api;
