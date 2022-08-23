(() => {
    const makeBeforeLoginTemplate = () => {
        const template = document.getElementById('beforeLoginTemplate').innerHTML
        document.getElementById('loginContainer').innerHTML = template
        document.querySelector("#login").addEventListener('click', () => loginButtonEvent())
    }

    const makeAfterLoginTemplate = (name, email) => {
        let template = document.getElementById('afterLoginTemplate').innerHTML
        template = template.replace('{name}', name)
        template = template.replace('{email}', email)
        document.getElementById('loginContainer').innerHTML = template
        document.querySelector("#logout").addEventListener('click', () => logoutButtonEvent())
    }

    const loginButtonEvent = () => {
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
                    document.cookie = 'name=' + body.response.name
                    document.cookie = 'email=' + body.response.email
                    makeAfterLoginTemplate(body.response.name, body.response.email)
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    }

    const logoutButtonEvent = () => {
        fetch('/api/v1/auth/logout', {
            method: 'POST'
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    alert('로그아웃에 성공하셨습니다.')
                    makeBeforeLoginTemplate()
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    }

    const setPostsList = (list) => {
        const createElementInnerText = (tagName, text) => {
            const element = document.createElement(tagName)
            element.innerText = text
            return element
        }

        const createIndexElement = (index) => {
            return createElementInnerText('td', index)
        }

        const createTitleElement = (title, postsId) => {
            const element = createElementInnerText('td', title)
            element.addEventListener('mouseover', (e) => {
                e.target.style.cursor = 'pointer'
            })
            element.addEventListener('click', () => {
                window.location.href = '/posts/' + postsId
            })
            return element
        }

        const createUserEmailElement = (email) => {
            const element = createElementInnerText('td', email)
            element.addEventListener('mouseover', (e) => {
                e.target.style.cursor = 'pointer'
            })
            element.addEventListener('click', () => {
                window.location.href = '/users/' + email
            })
            return element
        }

        const createViewCountElement = (viewCount) => {
            return createElementInnerText('td', viewCount)
        }

        const tbody = document.getElementById('posts-list')
        list.forEach((element, index) => {
            const node = document.createElement('tr')
            node.appendChild(createIndexElement(index + 1))
            node.appendChild(createTitleElement(element.title, element.postsId))
            node.appendChild(createViewCountElement(element.viewCount))
            node.appendChild(createUserEmailElement(element.userEmail))
            tbody.appendChild(node)
        })
    }

    const loadData = () => {
        const loggedInCheck = body => {
            if (body.success) {
                if (body.response) {
                    const name = document.cookie.split('; ').find(s => s.startsWith('name'))
                    const email = document.cookie.split('; ').find(s => s.startsWith('email'))
                    makeAfterLoginTemplate(name.split('=')[1], email.split('=')[1])
                } else {
                    makeBeforeLoginTemplate()
                }
            } else {
                alert(body.message)
            }
        }
        document.querySelector("#login").addEventListener('click', () => loginButtonEvent())

        fetch('/api/v1/auth/logged-in', {
            method: 'GET'
        }).then(response => {
            response.json().then(body => {
                loggedInCheck(body)
            })
        }).catch(error => console.log(error))


        fetch('/api/v1/posts', {
            method: 'GET'
        }).then((response) => {
            response.json().then(body => {
                if (body.success === true) {
                    setPostsList(body.response)
                } else {
                    console.log('조회에 실패하였습니다.')
                }
            })
        }).catch((error) => console.log(error))
    }
    loadData()
})()