// src/App.jsx
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import LoginForm from './components/LoginForm/LoginForm';
import RegisterForm from './components/RegisterForm/RegisterForm';
import HomePage from "./components/HomePage/HomePage";
import CatalogPage from "./components/CatalogPage/CatalogPage";
import BookPage from "./components/BookPage/BookPage";
import LibraryPage from "./components/LibraryPage/LibraryPage";
import ProfilePage from "./components/ProfilePage/ProfilePage";
import RecommendationsPage from "./components/RecommendationsPage/RecommendationsPage";
import AuthorPage from "./components/AuthorPage/AuthorPage";
import SeriesPage from "./components/SeriesPage/SeriesPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage/>}/>
                <Route path="/home" element={<HomePage/>}/>
                <Route path="/login" element={<LoginForm/>}/>
                <Route path="/register" element={<RegisterForm/>}/>
                <Route path="/catalog" element={<CatalogPage/>}/>
                <Route path="/books/:id" element={<BookPage/>}/>
                <Route path="/library" element={<LibraryPage/>}/>
                <Route path="/profile" element={<ProfilePage/>}/>
                <Route path="/recommendations" element={<RecommendationsPage/>}/>
                <Route path="/authors/:id" element={<AuthorPage/>}/>
                <Route path="/series/:id" element={<SeriesPage/>}/>

                {/* тут ваші інші захищені маршрути */}
            </Routes>
        </BrowserRouter>
    );
}

export default App;
