(() => {
    document.querySelector("#submit").addEventListener('click', () => {
        fetch('/api/v1/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            })
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    alert('회원가입 성공하셨습니다.')
                    window.location.href = '/'
                } else {
                    alert(body.message)
                }
            })
        }).catch(error => console.log(error))
    })
})()