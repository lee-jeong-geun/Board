(() => {
    document.getElementById('comment-save').addEventListener('click', () => {
        const urlArray = window.location.href.split('/')

        fetch('/api/v1/comment', {
            method: 'POST',
                headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: document.getElementById('comment-input').value,
                postsId: urlArray[urlArray.length - 1]
            })
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    alert('댓글 작성 성공하셨습니다.')
                    location.reload()
                } else {
                    alert(body.message)
                }
            })
        }).catch(error => console.log(error))
    })

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

        const setComment = response => {
            const commentList = document.getElementById('comment-list')
            response.forEach(data => {
                const element = document.createElement('tr')
                element.appendChild(createElementInnerText('td', data.userEmail))
                element.appendChild(createElementInnerText('td', data.content))
                commentList.appendChild(element)
            })
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

        fetch('/api/v1/comment/' + postsId, {
            method: 'GET'
        }).then(response => {
            response.json().then(body => {
                if (body.success) {
                    setComment(body.response)
                } else {
                    alert(body.message)
                }
            })
        }).catch(error => console.log(error))

    }
    loadData()

})()