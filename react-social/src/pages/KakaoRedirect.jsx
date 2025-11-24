import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";

export default function KakaoRedirect() {
  const [searchParams] = useSearchParams();
  const code = searchParams.get("code");

  useEffect(() => {
    if (code) {
      console.log("인가코드:", code);

      // 백엔드로 code 전송
      fetch("http://localhost:8080/kakao/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code }),
      })
        .then((res) => res.json())
        .then((data) => {
          console.log("백엔드 로그인 완료:", data);
        });
    }
  }, [code]);

  return <div>카카오 로그인 처리중...</div>;
}
