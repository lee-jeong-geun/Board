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
                    } else {
                        alert('로그아웃에 성공하셨습니다.')
                        e.target.textContent = '로그인'
                    }
                } else {
                    alert(body.message)
                }
            })
        }).catch((error) => console.log(error))
    })

    const setPostsList = (list) => {
        const tbody = document.getElementById('posts-list')
        list.forEach((element, index) => {
            const node = document.createElement('tr')
            let childNode = document.createElement('th')
            childNode.innerText = index + 1;
            node.appendChild(childNode);

            childNode = document.createElement('th')
            childNode.innerText = element.title;
            node.appendChild(childNode);

            childNode = document.createElement('th')
            childNode.innerText = element.userName;
            node.appendChild(childNode);

            tbody.appendChild(node);
        })
    }

    const loadData = () => {
        fetch('/api/v1/posts', {
            method: 'GET'
        }).then((response) => {
            response.json().then((body) => {
                if (body.success === true) {
                    setPostsList(body.postsList)
                } else {
                    console.log('조회에 실패하였습니다.')
                }
            })
        }).catch((error) => console.log(error))
    }
    loadData()
})()