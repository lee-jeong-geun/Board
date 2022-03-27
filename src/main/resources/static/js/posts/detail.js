(() => {
    const createElementInnerText = (tagName, text) => {
        const element = document.createElement(tagName)
        element.innerText = text
        return element
    }

    const loadData = () => {
        const setPosts = response => {
            document.getElementById('title').appendChild(createElementInnerText('p', response.title))
            document.getElementById('author').appendChild(createElementInnerText('p', response.userEmail))
            document.getElementById('content').appendChild(createElementInnerText('p', response.content))
        }

        const urlArray = window.location.href.split('/')
        const postsId = urlArray[urlArray.length - 1]

        fetch('/api/v1/posts/' + postsId, {
            method: 'GET'
        }).then(response => {
            response.json().then(body => {
                if (body.success) {
                    setPosts(body.response)
                } else {
                    alert(body.message)
                }
            })
        }).catch(error => console.log(error))

    }
    loadData()

})()