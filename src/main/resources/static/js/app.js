// 전화번호 필드 제한 조건
const phoneInput = document.getElementById('phone');

phoneInput.addEventListener('input', () => {
  // 숫자가 아니면 제거
  phoneInput.value = phoneInput.value.replace(/\D/g, '');

  // 11자리 이상 입력 방지
  if (phoneInput.value.length > 11) {
    phoneInput.value = phoneInput.value.slice(0, 11);
  }

  // 11자리 체크 및 커스텀 메시지 설정
  if (phoneInput.value.length > 0 && phoneInput.value.length !== 11) {
    phoneInput.setCustomValidity("전화번호는 기호를 제외한 숫자 11자리여야 합니다.");
  } else {
    phoneInput.setCustomValidity("");
  }
});


document.addEventListener('DOMContentLoaded', () => {

    // 변수 선언
    const emailInput = document.getElementById('email');
    const sendCodeBtn = document.getElementById('send-code-btn');
    const verificationCodeInput = document.getElementById('verificationCode');
    const verifyCodeBtn = document.getElementById('verify-code-btn');
    const finalSignupBtn = document.getElementById('final-signup-btn');

    // 인증번호 전송 버튼 클릭 이벤트
    sendCodeBtn.addEventListener('click', async () => {
        const email = emailInput.value;
        if (!email) {
            alert('이메일 주소를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch('/api/send-verification-code', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email })
            });

            if (response.ok) {
                alert('인증번호가 이메일로 전송되었습니다. 5분 안에 입력해주세요.');
                verificationCodeInput.style.display = 'block';
                verifyCodeBtn.style.display = 'inline-block';
                sendCodeBtn.disabled = true;
            } else {
                alert('인증번호 전송에 실패했습니다. 다시 시도해주세요.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 오류가 발생했습니다. 관리자에게 문의해주세요.');
        }
    });

    // 인증번호 확인 버튼 클릭 이벤트
    verifyCodeBtn.addEventListener('click', async () => {
        const email = emailInput.value;
        const code = verificationCodeInput.value;
        if (!code) {
            alert('인증번호를 입력해주세요.');
            return;
        }

        try {
            const response = await fetch('/api/verify-code', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: email, code: code })
            });

            if (response.ok) {
                alert('이메일 인증이 성공적으로 완료되었습니다.');
                finalSignupBtn.disabled = false;

                // ⭐ 이메일 필드를 disabled 대신 readonly로 변경
                emailInput.readOnly = true;
                emailInput.style.backgroundColor = '#e9e9e9'; // 시각적으로 비활성화된 것처럼 보이게 함

                verificationCodeInput.disabled = true;
                sendCodeBtn.disabled = true;
                verifyCodeBtn.disabled = true;
            } else {
                const errorMessage = await response.text();
                alert(`인증 실패: ${errorMessage}`);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 오류가 발생했습니다.');
        }
    });

    // 최종 가입 버튼 클릭 이벤트
    document.getElementById('final-signup-btn').addEventListener('click', (event) => {
        if (finalSignupBtn.disabled) {
            event.preventDefault(); // 버튼이 비활성화 상태면 폼 전송 막기
            alert('이메일 인증을 먼저 완료해주세요.');
        }
    });
});