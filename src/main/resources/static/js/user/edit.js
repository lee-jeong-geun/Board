(() => {
    document.getElementById('edit').addEventListener('click', () => {

    })

    const setInform = (response) => {
        const nameElement = document.getElementById('name')
        nameElement.innerText = response.name
    }

    const loadData = () => {
        const urlArray = window.location.href.split("/")
        const email = urlArray[urlArray.length - 1]
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