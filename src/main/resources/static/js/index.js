(() => {
    document.querySelector("#login").addEventListener('click', () => {
        fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: document.getElementById('email').value,
                password: document.getElementById('password').value
            })
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    alert('로그인에 성공하셨습니다.')
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    })
})()