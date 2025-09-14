document.addEventListener('DOMContentLoaded', function() {


//------------------------- /signUp 입력칸 제한 -----------------------------------------------------------------
  const form = document.getElementById('signupForm');
  const usernameInput = document.querySelector('input[name="username"]');
  const phoneInput = document.getElementById('phone');
  const emailInput = document.getElementById('email');
  const passwordInput = document.querySelector('input[name="password"]');
  const passwordConfirmInput = document.querySelector('input[name="passwordConfirm"]');

  // Helper function to show validation errors
  const showError = (input, message) => {
    const section = input.closest('.input-section');
    const infoText = section.querySelector('.input-info');
    infoText.textContent = message;
    infoText.style.color = '#e33d3d'; // Set a red color for errors
  };

  // Helper function to clear validation errors
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
      infoText.textContent = '8-20자까지 모든 문자+숫자+특수문자 허용 영문 대/소문자는 구분되며, 사용 불가능한 특수문자: >, <, _, ", \'';
    } else if (input === passwordConfirmInput) {
      infoText.textContent = '비밀번호와 동일하게 입력해 주세요.';
    }
    infoText.style.color = '#888'; // Reset to default color
  };

  // Validate all inputs before submission
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

  // Attach the submit event listener to the form
  form.addEventListener('submit', (e) => {
    // Prevent default form submission to perform validation first
    e.preventDefault();

    if (validateInputs()) {
      // If validation passes, the form can be submitted to the backend
      form.submit();
      console.log('Form is valid and will be submitted.');
    }
  });

  // Add real-time validation on input change for better user experience
  usernameInput.addEventListener('input', () => clearError(usernameInput));
  phoneInput.addEventListener('input', () => {
    // Automatically remove hyphens as user types
    phoneInput.value = phoneInput.value.replace(/[^0-9]/g, '');
    clearError(phoneInput);
  });
  emailInput.addEventListener('input', () => clearError(emailInput));
  passwordInput.addEventListener('input', () => clearError(passwordInput));
  passwordConfirmInput.addEventListener('input', () => clearError(passwordConfirmInput));

    //------------------------- /signUpAgree 체크박스 필수 확인-----------------------------------------------------------------
    const termsAllCheckbox = document.getElementById('termsAll');
    const requiredCheckboxes = document.querySelectorAll('.terms-checkbox.required');
    const marketingCheckbox = document.querySelector('input[name="marketingAgreed"]');
    const nextBtn = document.getElementById('nextBtn');
    const termsForm = document.getElementById('termsForm'); // form 태그 선택자 추가

    // 모든 필수 체크박스 상태 확인 함수
    function checkRequired() {
        let allRequiredChecked = true;
        requiredCheckboxes.forEach(checkbox => {
            if (!checkbox.checked) {
                allRequiredChecked = false;
            }
        });

        return allRequiredChecked; // 추가: true/false 반환
    }

    // '모두 동의' 체크박스 이벤트 리스너
    termsAllCheckbox.addEventListener('change', function() {
        const isChecked = this.checked;
        requiredCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });
        marketingCheckbox.checked = isChecked;
        checkRequired();
    });

    // 개별 체크박스 이벤트 리스너
    document.querySelectorAll('.terms-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const allChecked = Array.from(document.querySelectorAll('.terms-checkbox')).every(cb => cb.checked);
            termsAllCheckbox.checked = allChecked;

            checkRequired();
        });
    });

    // 폼 제출(submit) 이벤트 리스너 추가: 이 부분이 핵심입니다.
    termsForm.addEventListener('submit', function(event) {
        if (!checkRequired()) { // 필수 체크박스가 모두 체크되었는지 확인
            event.preventDefault(); // 폼 제출을 막습니다.
            alert("필수 약관에 모두 동의해야 다음 단계로 진행할 수 있습니다.");
        }
    });

    // 페이지 로드 시 초기 상태 확인
    checkRequired();
});