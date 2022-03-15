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
})()