(() => {
    document.querySelector("#login").addEventListener('click', (e) => {
        const state = e.target.textContent;
        let url
        let init
        if (state === '로그인') {
            url = '/api/v1/auth/login'
            init = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: document.getElementById('email').value,
                    password: document.getElementById('password').value
                })
            }
        } else {
            url = '/api/v1/auth/logout'
            init = {
                method: 'POST'
            }
        }

        fetch(url, init).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    if (state === '로그인') {
                        alert('로그인에 성공하셨습니다.')
                        e.target.textContent = '로그아웃'
                        const element = document.createElement('a')
                        element.innerHTML = '<a href="/posts/create" role="button" class="btn btn-primary">글 등록</a>'
                        e.target.parentElement.appendChild(element)
                    } else {
                        alert('로그아웃에 성공하셨습니다.')
                        e.target.textContent = '로그인'
                        e.target.parentElement.removeChild(e.target.parentElement.lastChild)
                    }
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    })

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

        const tbody = document.getElementById('posts-list')
        list.forEach((element, index) => {
            const node = document.createElement('tr')
            node.appendChild(createIndexElement(index + 1));
            node.appendChild(createTitleElement(element.title, element.postsId));
            node.appendChild(createUserEmailElement(element.userEmail));
            tbody.appendChild(node);
        })
    }

    const loadData = () => {
        fetch('/api/v1/posts', {
            method: 'GET'
        }).then((response) => {
            response.json().then((body) => {
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