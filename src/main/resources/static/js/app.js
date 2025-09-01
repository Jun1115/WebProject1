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
