(() => {
    document.getElementById('edit').addEventListener('click', () => {
        const urlArray = window.location.href.split("/")
        const email = urlArray[urlArray.length - 2]
        const url = '/api/v1/users/' + email

        fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: document.getElementById('name').value,
                password: document.getElementById('password').value
            })
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    alert('업데이트 성공하셨습니다.')
                    location.reload()
                } else {
                    alert(body.message)
                }
            })
        }).catch(error => console.log(error))
    })

    const setInform = (response) => {
        const nameElement = document.getElementById('name')
        nameElement.value = response.name
    }

    const loadData = () => {
        const urlArray = window.location.href.split('/')
        const email = urlArray[urlArray.length - 2]
        const url = '/api/v1/users/' + email

        fetch(url, {
            method: 'GET'
        }).then(response => {
            response.json().then(body => {
                if (body.success === true) {
                    setInform(body.response)
                } else {
                    console.error(body.message)
                }
            })
        }).catch(error => console.log(error))
    }
    loadData()
})()