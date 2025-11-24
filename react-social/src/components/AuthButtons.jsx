// react-kakao-apple-auth.js (React + JavaScript)
// Kakao Login (OAuth URL 방식 - 백엔드에서 토큰 교환) + Apple Login

/*
====================================
1) public/index.html 설정 (Kakao SDK 필요 없음)
====================================
<head></head>

====================================
2) .env 설정
====================================
VITE_KAKAO_CLIENT_ID=카카오_REST_API_KEY
VITE_KAKAO_REDIRECT_URI=https://your-site.com/auth/kakao/callback

VITE_APPLE_CLIENT_ID=com.example.web
VITE_APPLE_REDIRECT_URI=https://your-site.com/auth/apple/callback

====================================
3) 사용법
====================================
import AuthButtons from './react-kakao-apple-auth-js';

<AuthButtons />

====================================
4) Kakao 방식 설명(A 방식)
====================================
- 프론트에서 카카오 로그인 URL로 이동
- 로그인 완료 후 redirect_uri 로 code 전달
- 백엔드(Spring Boot)가 code → access_token 교환
*/

import React from 'react';

// =============================
// Kakao OAuth URL 방식 (A 방식)
// =============================
export function KakaoLoginButton() {
  const kakaoLogin = () => {
    const clientId = import.meta.env.VITE_KAKAO_CLIENT_ID;
    const redirectUri = import.meta.env.VITE_KAKAO_REDIRECT_URI;
    const scope = "profile_nickname account_email profile_image";

    if (!clientId || !redirectUri) {
      alert("Kakao 환경 변수 설정 필요");
      return;
    }

    const kakaoUrl =
      `https://kauth.kakao.com/oauth/authorize?client_id=${clientId}` +
      `&redirect_uri=${encodeURIComponent(redirectUri)}` +
      `&response_type=code&scope=${scope}`;

    window.location.href = kakaoUrl;
  };

  return (
    <button
      onClick={kakaoLogin}
      className="px-4 py-2 bg-yellow-300 rounded-xl font-semibold"
    >
      Kakao 로그인
    </button>
  );
}

// =============================
// Apple Login Button (OAuth redirect)
// =============================
export function AppleLoginButton() {
  const handleAppleLogin = () => {
    const clientId = import.meta.env.VITE_APPLE_CLIENT_ID;
    const redirect = import.meta.env.VITE_APPLE_REDIRECT_URI;

    if (!clientId || !redirect) {
      alert('Apple 환경 변수 설정 필요');
      return;
    }

    const url =
      `https://appleid.apple.com/auth/authorize?` +
      `response_type=code&client_id=${encodeURIComponent(clientId)}` +
      `&redirect_uri=${encodeURIComponent(redirect)}` +
      `&scope=name%20email&response_mode=form_post`;

    window.location.href = url;
  };

  return (
    <button
      onClick={handleAppleLogin}
      className="px-4 py-2 bg-black text-white rounded-xl font-semibold"
    >
      Apple 로그인
    </button>
  );
}

// =============================
// AuthButtons 묶음
// =============================
export default function AuthButtons() {
  return (
    <div className="flex flex-col gap-4 p-4">
      <KakaoLoginButton />
      <AppleLoginButton />
    </div>
  );
}
