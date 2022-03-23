(() => {
    document.getElementById('save').addEventListener('click', () => {
        const init = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        }
        fetch('/api/v1/posts', init).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    console.log('글이 등록 되었습니다.')
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    })

    const init = () => {
         const loggedInCheck = body => {
            if (body.success) {
                if (!body.response) {
                    alert('로그인 상태가 아닙니다')
                    window.location.href = '/'
                }
            } else {
                alert(body.message)
            }
        }

        const url = '/api/v1/auth/logged-in'
        const init = {
            method: 'GET'
        }
        fetch(url, init).then(response => {
            response.json().then(body => {
                loggedInCheck(body)
            })
        }).catch(error => console.log(error))
    }
    init()
})()