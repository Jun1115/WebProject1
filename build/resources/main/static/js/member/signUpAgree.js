document.addEventListener('DOMContentLoaded', function() {


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