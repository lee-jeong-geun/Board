(() => {

    const createElementInnerText = (tagName, text) => {
        const element = document.createElement(tagName)
        element.innerText = text
        return element
    }

    const setInform = (response) => {
        const informBody = document.getElementById('user-inform').firstElementChild
        const tagMap = {
            '이름': response.name,
            '이메일': response.email,
            '최근 로그인 시간': response.lastLogIn.replace('T', ' ')
        }
        for (let key in tagMap) {
            let childNode = document.createElement('tr')
            childNode.append(createElementInnerText('td', key))
            childNode.append(createElementInnerText('td', tagMap[key]))
            informBody.appendChild(childNode)
        }
    }

    const setPostsList = (list) => {
        const tbody = document.getElementById('posts-list')
        list.forEach((element, index) => {
            const childNode = document.createElement('tr')
            childNode.append(createElementInnerText('td', index + 1))

            const postsElement = createElementInnerText('td', element.title)
            postsElement.addEventListener('click', () => {
                window.location.href = '/posts/' + element.postsId
            })
            postsElement.addEventListener('mouseover', (e) => {
                e.target.style.cursor = 'pointer'
            })
            childNode.append(postsElement)
            tbody.appendChild(childNode)
        })
    }

    const setCommentList = (list) => {
        const tbody = document.getElementById('comment-list')
        list.forEach((element, index) => {
            const childNode = document.createElement('tr')
            childNode.append(createElementInnerText('td', index + 1))

            const commentElement = createElementInnerText('td', element.content)
            commentElement.addEventListener('click', () => {
                window.location.href = '/posts/' + element.postsId
            })
            commentElement.addEventListener('mouseover', (e) => {
                e.target.style.cursor = 'pointer'
            })
            childNode.append(commentElement)
            tbody.appendChild(childNode)
        })
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
                    setPostsList(body.response.posts)
                    setCommentList(body.response.comment)
                } else {
                    console.error(body.message)
                }
            })
        }).catch(error => console.log(error))
    }
    loadData()
})()