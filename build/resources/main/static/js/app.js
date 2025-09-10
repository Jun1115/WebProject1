document.addEventListener('DOMContentLoaded', () => {

    // Helper function to show a custom message box
    function showMessage(text, type = 'success') {
        const messageBox = document.getElementById('message-box');
        if (messageBox) {
            messageBox.textContent = text;
            messageBox.className = 'message-box';
            if (type === 'error') {
                messageBox.classList.add('error');
            }
            messageBox.style.display = 'block';
            setTimeout(() => {
                messageBox.style.display = 'none';
            }, 3000);
        }
    }

    // Handle phone number input validation
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', () => {
            // Remove all non-numeric characters
            phoneInput.value = phoneInput.value.replace(/\D/g, '');

            // Limit input to 11 characters
            if (phoneInput.value.length > 11) {
                phoneInput.value = phoneInput.value.slice(0, 11);
            }

            // Set custom validity message for non-11 digit numbers
            if (phoneInput.value.length > 0 && phoneInput.value.length !== 11) {
                phoneInput.setCustomValidity("전화번호는 기호를 제외한 숫자 11자리여야 합니다.");
            } else {
                phoneInput.setCustomValidity("");
            }
        });
    }

    // Get elements for email verification logic
    const emailInput = document.getElementById('email');
    const sendCodeBtn = document.getElementById('send-code-btn');
    const verificationCodeInput = document.getElementById('verificationCode');
    const verifyCodeBtn = document.getElementById('verify-code-btn');
    const finalSignupBtn = document.getElementById('final-signup-btn');
    const verificationGroup = document.getElementById('verification-group');
    
    // Hide verification group initially
    if (verificationGroup) {
      verificationGroup.style.display = 'none';
    }
    
    // Handle "Send Verification Code" button click
    if (sendCodeBtn) {
        sendCodeBtn.addEventListener('click', async () => {
            const email = emailInput.value;
            if (!email) {
                showMessage('이메일 주소를 입력해주세요.', 'error');
                return;
            }

            try {
                // Simulate API call
                const response = await fetch('/api/send-verification-code', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email })
                });

                if (response.ok) {
                    showMessage('인증번호가 이메일로 전송되었습니다. 5분 안에 입력해주세요.');
                    if (verificationGroup) {
                      verificationGroup.style.display = 'flex';
                    }
                    sendCodeBtn.disabled = true;
                } else {
                    showMessage('인증번호 전송에 실패했습니다. 다시 시도해주세요.', 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('서버 오류가 발생했습니다. 관리자에게 문의해주세요.', 'error');
            }
        });
    }

    // Handle "Verify Code" button click
    if (verifyCodeBtn) {
        verifyCodeBtn.addEventListener('click', async () => {
            const email = emailInput.value;
            const code = verificationCodeInput.value;
            if (!code) {
                showMessage('인증번호를 입력해주세요.', 'error');
                return;
            }

            try {
                // Simulate API call
                const response = await fetch('/api/verify-code', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email: email, code: code })
                });

                if (response.ok) {
                    showMessage('이메일 인증이 성공적으로 완료되었습니다.');
                    if (finalSignupBtn) {
                      finalSignupBtn.disabled = false;
                    }

                    // Disable email and verification fields after successful verification
                    emailInput.readOnly = true;
                    emailInput.style.backgroundColor = '#e9e9e9'; 

                    verificationCodeInput.disabled = true;
                    sendCodeBtn.disabled = true;
                    verifyCodeBtn.disabled = true;
                } else {
                    const errorMessage = await response.text();
                    showMessage(`인증 실패: ${errorMessage}`, 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showMessage('서버 오류가 발생했습니다.', 'error');
            }
        });
    }

    // Prevent form submission if not verified
    if (finalSignupBtn) {
        finalSignupBtn.addEventListener('click', (event) => {
            if (finalSignupBtn.disabled) {
                event.preventDefault();
                showMessage('이메일 인증을 먼저 완료해주세요.', 'error');
            }
        });
    }
});
