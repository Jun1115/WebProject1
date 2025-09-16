document.addEventListener('DOMContentLoaded', function() {

  //------------------------- /signUp 입력칸 제한 -----------------------------------------------------------------
  const form = document.getElementById('signupForm');
  const usernameInput = document.querySelector('input[name="username"]');
  const phoneInput = document.getElementById('phone');
  const emailInput = document.getElementById('email');
  const passwordInput = document.querySelector('input[name="password"]');
  const passwordConfirmInput = document.querySelector('input[name="passwordConfirm"]');

  // 이메일 인증 관련 요소
  const verifyEmailRequestBtn = document.getElementById('verifyEmailRequest');
  const verifyEmailConfirmBtn = document.getElementById('verifyEmailConfirm');
  const emailCodeInput = document.getElementById('emailCode');
  const emailVerifyMessage = document.getElementById('emailVerifyMessage');

  // 이메일 인증 상태를 저장하는 변수
  let emailVerified = false;

  // 사용자가 입력한 내용에 문제가 있을 때 시각적으로 피드백을 주는 역할
  const showError = (input, message) => {
    const section = input.closest('.input-section');
    const infoText = section.querySelector('.input-info');
    infoText.textContent = message;
    infoText.style.color = '#e33d3d'; // Set a red color for errors
  };

  const clearError = (input) => {
    const section = input.closest('.input-section');
    const infoText = section.querySelector('.input-info');
    // Reset the text based on the input name
    if (input === usernameInput) {
      infoText.textContent = '이름을 입력해 주세요.';
    } else if (input === phoneInput) {
      infoText.textContent = '회원 여부 확인을 위해 전화번호 입력이 필요합니다. (-) 기호를 제외한 전화번호를 입력해 주세요.';
    } else if (input === emailInput) {
      infoText.textContent = '이메일 인증 및 회원 여부 확인을 위해 이메일 입력이 필요합니다.';
    } else if (input === passwordInput) {
      infoText.textContent = '비밀번호는 8~20자이며, 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다. 영문 대/소문자는 구분되며, 사용 불가능한 특수문자: >, <, _, ", \'';
    } else if (input === passwordConfirmInput) {
      infoText.textContent = '비밀번호와 동일하게 입력해 주세요.';
    }
    infoText.style.color = '#888'; // Reset to default color
  };

  // 입력 필드 유효성 검사 로직
  const validateInputs = () => {
    let isValid = true;

    // Validate phone number
    const phoneValue = phoneInput.value.trim();
    if (phoneValue === '' || !/^\d{11}$/.test(phoneValue)) {
      showError(phoneInput, '유효한 전화번호(11자리 숫자)를 입력해 주세요.');
      isValid = false;
    } else {
      clearError(phoneInput);
    }

    // Validate email format
    const emailValue = emailInput.value.trim();
    if (emailValue === '' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailValue)) {
      showError(emailInput, '유효한 이메일 주소를 입력해 주세요.');
      isValid = false;
    } else {
      clearError(emailInput);
    }

    // Validate password
    const passwordValue = passwordInput.value.trim();
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+=\-{}[\]:;"'<,>.?/|~`])(?!.*[<>"'_]).{8,20}$/;
    if (passwordValue === '' || passwordValue.length < 8 || passwordValue.length > 20 || !passwordRegex.test(passwordValue)) {
      showError(passwordInput, '비밀번호는 8~20자이며, 영문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다. 단, 특수문자(<, >, ", \', _)를 제외해야 합니다.');
      isValid = false;
    } else {
      clearError(passwordInput);
    }

    // Validate password confirmation
    const passwordConfirmValue = passwordConfirmInput.value.trim();
    if (passwordConfirmValue === '' || passwordConfirmValue !== passwordValue) {
      showError(passwordConfirmInput, '비밀번호가 일치하지 않습니다.');
      isValid = false;
    } else {
      clearError(passwordConfirmInput);
    }

    // If all validations pass, return true
    return isValid;
  };

  // 실시간 유효성 검사 (사용자 경험 개선)
  usernameInput.addEventListener('input', () => clearError(usernameInput));
  phoneInput.addEventListener('input', () => {
    // Automatically remove hyphens as user types
    phoneInput.value = phoneInput.value.replace(/[^0-9]/g, '');
    clearError(phoneInput);
  });
  emailInput.addEventListener('input', () => {
    clearError(emailInput);
    emailVerified = false; // 이메일 변경 시 인증 상태 초기화
  });
  passwordInput.addEventListener('input', () => clearError(passwordInput));
  passwordConfirmInput.addEventListener('input', () => clearError(passwordConfirmInput));

  //------------------------- 이메일 인증 로직 -----------------------------------------------------------------

  let verificationTimer;

  // 인증 요청 버튼 클릭 이벤트
  verifyEmailRequestBtn.addEventListener('click', async () => {
    const email = emailInput.value.trim();

    // 이메일 유효성 검사
    if (email === '' || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      showEmailMessage('유효한 이메일 주소를 입력해주세요.', 'error');
      return;
    }

    // 버튼 비활성화 및 로딩 상태 표시
    verifyEmailRequestBtn.disabled = true;
    verifyEmailRequestBtn.textContent = '전송 중...';
    emailCodeInput.disabled = false;
    verifyEmailConfirmBtn.disabled = false;

    try {
      const response = await fetch('/member/api/send-verification-code', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: email }),
      });

      if (response.ok) {
        showEmailMessage('인증번호가 전송되었습니다. 5분 안에 입력해주세요.', 'info');
        // 타이머 시작
        startTimer(300); // 300초 = 5분
      } else {
        const errorText = await response.text();
        showEmailMessage(`인증번호 전송 실패: ${errorText}`, 'error');
        // 실패 시 버튼 다시 활성화
        verifyEmailRequestBtn.disabled = false;
        verifyEmailRequestBtn.textContent = '인증 요청';
      }
    } catch (error) {
      console.error('Error sending verification code:', error);
      showEmailMessage('서버 오류로 인증번호 전송에 실패했습니다.', 'error');
      verifyEmailRequestBtn.disabled = false;
      verifyEmailRequestBtn.textContent = '인증 요청';
    }
  });

  // 인증 확인 버튼 클릭 이벤트
  verifyEmailConfirmBtn.addEventListener('click', async () => {
    const email = emailInput.value.trim();
    const code = emailCodeInput.value.trim();

    if (code === '') {
      showEmailMessage('인증번호를 입력해주세요.', 'error');
      return;
    }

    // 버튼 비활성화 및 로딩 상태 표시
    verifyEmailConfirmBtn.disabled = true;
    verifyEmailConfirmBtn.textContent = '확인 중...';

    try {
      const response = await fetch('/member/api/verify-code', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: email, code: code }),
      });

      if (response.ok) {
        showEmailMessage('인증이 완료되었습니다.', 'success');
        emailVerified = true;
        // 성공 시 모든 이메일 관련 필드 및 버튼 비활성화
        emailInput.readOnly = true;
        emailCodeInput.readOnly = true;
        verifyEmailRequestBtn.disabled = true;
        verifyEmailConfirmBtn.disabled = true;
        verifyEmailRequestBtn.textContent = '인증 완료';
        stopTimer();
      } else {
        const errorText = await response.text();
        showEmailMessage(`인증 실패: ${errorText}`, 'error');
        emailVerified = false;
        verifyEmailConfirmBtn.disabled = false;
        verifyEmailConfirmBtn.textContent = '인증 확인';
      }
    } catch (error) {
      console.error('Error verifying code:', error);
      showEmailMessage('서버 오류로 인증 확인에 실패했습니다.', 'error');
      verifyEmailConfirmBtn.disabled = false;
      verifyEmailConfirmBtn.textContent = '인증 확인';
    }
  });

  // 이메일 메시지 표시 함수
  function showEmailMessage(message, type) {
    emailVerifyMessage.textContent = message;
    if (type === 'error') {
      emailVerifyMessage.style.color = '#e33d3d';
    } else if (type === 'success') {
      emailVerifyMessage.style.color = '#28a745';
    } else { // info
      emailVerifyMessage.style.color = '#888';
    }
  }

  // 타이머 시작 함수
  function startTimer(duration) {
    let timer = duration;
    const minutes = Math.floor(timer / 60);
    const seconds = timer % 60;
    showEmailMessage(`인증번호가 전송되었습니다. 남은 시간: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`, 'info');

    verificationTimer = setInterval(() => {
      timer--;
      const minutes = Math.floor(timer / 60);
      const seconds = timer % 60;
      showEmailMessage(`인증번호가 전송되었습니다. 남은 시간: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`, 'info');

      if (timer <= 0) {
        clearInterval(verificationTimer);
        showEmailMessage('인증 시간이 초과되었습니다. 다시 인증 요청을 해주세요.', 'error');
        emailCodeInput.value = '';
        verifyEmailRequestBtn.disabled = false;
        verifyEmailRequestBtn.textContent = '인증 요청';
        verifyEmailConfirmBtn.disabled = true;
      }
    }, 1000);
  }

  // 타이머 중지 함수
  function stopTimer() {
    clearInterval(verificationTimer);
  }

  // 폼 제출 이벤트 리스너
  form.addEventListener('submit', (e) => {
    e.preventDefault();

    if (!validateInputs()) {
      return;
    }

    if (!emailVerified) {
      showEmailMessage('이메일 인증을 완료해주세요.', 'error');
      return;
    }

    // 모든 유효성 검사를 통과하면 폼 제출
    form.submit();
    console.log('Form is valid and will be submitted.');
  });
});