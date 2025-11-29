import { Routes, Route } from "react-router-dom";
import AuthButtons from "./components/AuthButtons";
import KakaoRedirect from "./pages/KakaoRedirect";
import AppleRedirect from "./pages/AppleRedirect";
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/" element={<AuthButtons />} />
      <Route path="/oauth/kakao/redirect" element={<KakaoRedirect />} />
      <Route path="/oauth/apple/redirect" element={<AppleRedirect />} />
    </Routes>
  )
}

export default App
