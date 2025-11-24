import { Routes, Route } from "react-router-dom";
import AuthButtons from "./components/AuthButtons";
import KakaoRedirect from "./pages/KakaoRedirect";
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/" element={<AuthButtons />} />
      <Route path="/oauth/kakao/redirect" element={<KakaoRedirect />} />
    </Routes>
  )
}

export default App
