var boards = []; // 배열 초기화
let currentPage = 1;
const commentsPerPage = 5;

$(document).ready(function () {
    $('#query').on('keypress', function (e) {
        if (e.key === 'Enter') {
            const searchOption = document.querySelector('input[name="search-option"]:checked').value;
            if (searchOption === 'assignee') {
                execSearchAssignee();
            } else if (searchOption === 'status') {
                execSearchStatus();
            } else if (searchOption === 'all') {
                execSearchAll();
            }
        }
    });

    $('#kanban-board').show();
    $('#explore-section').hide();

    $('.nav div.nav-see').on('click', function () {
        $('div.nav-see').addClass('active');
        $('div.nav-search').removeClass('active');

        $('#kanban-board').show();
        $('#explore-section').hide();
    });
    $('.nav div.nav-search').on('click', function () {
        $('div.nav-see').removeClass('active');
        $('div.nav-search').addClass('active');

        $('#kanban-board').hide();
        $('#explore-section').show();
    });

    // 문서 로드 완료 시 Sortable 초기화
    initializeSortable();
});

const auth = getToken();
if (!auth) {
    window.location.href = '/users/login-page';
}

$.ajax({
    url: 'http://localhost:8080/boards',
    type: 'GET',
    beforeSend: function (xhr) {
        xhr.setRequestHeader('Authorization', auth);
    },
    success: function (data) {
        console.log(data);
        boards = data.data; // 서버에서 받은 데이터를 boards에 할당
        console.log(boards); // boards 배열 확인
        boards.forEach(function (board) {
            addBoardToSidebar(board);
        });
    },
    error: function (error) {
        console.error('Error:', error);
    }
});


// 쿠키에서 특정 이름의 값을 가져오는 함수
function getCookie(name) {
    var value = "; " + document.cookie;
    console.log('Cookies:', value); // 디버깅을 위한 로그
    var parts = value.split("; " + name + "=");
    console.log('Parts:', parts); // 디버깅을 위한 로그
    if (parts.length === 2) {
        const cookieValue = parts.pop().split(";").shift();
        console.log('Encoded Cookie Value:', cookieValue); // 디버깅을 위한 로그
        const decodedCookieValue = decodeURIComponent(cookieValue);
        console.log('Decoded Cookie Value:', decodedCookieValue); // 디버깅을 위한 로그
        return decodedCookieValue;
    }
}

// 토큰을 가져오는 함수
function getToken() {

    let auth = getCookie('Authorization');

    if (auth === undefined) {
        return '';
    }
    return auth;
}

function deleteCookie(name) {
    document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function logout() {
    // 토큰 삭제
    deleteCookie('Authorization');
    window.location.href = '/users/login-page';
}

// 보드 생성 모달 보이기
function showBoardCreationModal() {
    var modal = document.getElementById('board-creation-modal');
    modal.style.display = 'block';
}

// 보드 생성 모달 숨기기
function hideBoardCreationModal() {
    var modal = document.getElementById('board-creation-modal');
    modal.style.display = 'none';
}

// 보드 생성 함수
function createBoard() {
    var title = document.getElementById('board-title').value;
    var content = document.getElementById('board-content').value;

    if (title.trim() === '') {
        document.getElementById('board-error').textContent = '보드 제목을 입력해주세요.';
        return;
    }

    var board = {
        boardName: title,
        boardInfo: content,
        columns: []
    };

    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/admin/boards',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(board),
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function (data) {
            console.log(data);
            var boardId = data.data.boardId;
            boards.push({...board, boardId});
            addBoardToSidebar({...board, boardId});
            hideBoardCreationModal();
            document.getElementById('board-title').value = '';
            document.getElementById('board-content').value = '';
            document.getElementById('board-error').textContent = '';
        },
        error: function () {
            document.getElementById(
                'board-error').textContent = '보드 생성 중 오류가 발생했습니다.';
        }
    });
}

// 사이드바에 보드 이름 추가
function addBoardToSidebar(board) {
    console.log(board);
    var boardList = document.getElementById('board-list');

    var boardItem = document.createElement('div');
    boardItem.classList.add('board-item');
    // 보드 제목과 수정 및 삭제 버튼 추가
    boardItem.innerHTML = `
        <span>${board.boardName}</span>
        <button class="edit-board-button" onclick="openEditBoardModal('${board.boardId}')">보드 수정</button>
        <button class="delete-board-button" onclick="confirmDeleteBoard('${board.boardId}')">보드 삭제</button>
        <button class="invite-user-button" onclick="showInviteUserModal('${board.boardId}')">사용자 초대</button>
    `;

    boardItem.addEventListener('click', function () {
        fetchBoard(board.boardId);
    });

    boardList.appendChild(boardItem);
}

// 보드 표시 함수
function displayBoard(board) {
    var kanbanBoard = document.getElementById('kanban-board');
    kanbanBoard.innerHTML = `
        <div class="board-content">
            <h2>${board.boardName}</h2>
            <p>${board.boardInfo}</p>
            <div class="columns" id="columns-${board.boardName}">
                <!-- 컬럼들이 여기에 추가됩니다 -->
            </div>
            <button onclick="showColumnCreationModal('${board.boardId}')">컬럼 추가</button>
        </div>
    `;
    // 저장된 컬럼들 표시
    fetchColumns(board.boardId)

    // Sortable 초기화
    initializeSortableColumns(kanbanBoard.querySelector('.board-content'));

}

// 보드 단건 조회 함수
function fetchBoard(boardId) {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    $.ajax({
        url: `http://localhost:8080/boards/${boardId}`,
        type: 'GET',
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function(data) {
            var board = data.data;
            console.log(board);
            displayBoard(board);
        },
        error: function() {
            console.error('보드 조회 중 오류가 발생했습니다.');
        }
    });
}

// 컬럼 전체 조회 함수
function fetchColumns(boardId) {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    $.ajax({
        url: 'http://localhost:8080/boards/' + boardId + '/columns',
        type: 'GET',
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function(data) {
            console.log("Fetched columns data:", data.data);
            var board = boards.find(b => b.boardId === parseInt(boardId, 10));
            if (board) {
                board.columns = data.data || [];
                // 각 컬럼에 대해 카드 조회
                Promise.all(board.columns.map(column => fetchCards(boardId, column.columnId)))
                .then(() => {
                    displayColumns(board);
                });
            }
        },
        error: function() {
            alert('컬럼 조회에 실패했습니다. 다시 시도해주세요.');
        }
    });
}
// 카드 조회 함수
function fetchCards(boardId, columnId) {
    const auth = getToken();
    return new Promise((resolve, reject) => {
        $.ajax({
            url: `http://localhost:8080/boards/${boardId}/columns/${columnId}/cards`,
            type: 'GET',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('Authorization', auth);
            },
            success: function(data) {
                var board = boards.find(b => b.boardId === parseInt(boardId, 10));
                var column = board.columns.find(c => c.columnId === parseInt(columnId, 10));
                if (column) {
                    column.cards = data.data || [];
                }
                resolve();
            },
            error: function() {
                console.error('카드 조회에 실패했습니다.');
                reject();
            }
        });
    });
}

// 보드 수정 모달 열기 함수
function openEditBoardModal(boardId) {
    var modal = document.getElementById('edit-board-modal');
    var board = boards.find(b => b.boardId === parseInt(boardId, 10));

    if (board) {
        document.getElementById('edit-board-name').value = board.boardName;
        document.getElementById('edit-board-description').value = board.boardInfo;

        // 저장 버튼에 클릭 이벤트 리스너 추가
        var saveButton = document.getElementById('save-board-button');
        saveButton.onclick = function () {
            saveBoardChanges(boardId);
            closeModal(modal); // 저장 후 모달 닫기
        };

        // 취소 버튼에 클릭 이벤트 리스너 추가
        var cancelButton = document.getElementById('cancel-edit-button');
        cancelButton.onclick = function () {
            closeModal(modal); // 취소 시 모달 닫기
        };

        // 모달 닫기 버튼에 클릭 이벤트 리스너 추가
        var closeButton = modal.querySelector('.close');
        closeButton.onclick = function () {
            closeModal(modal); // 닫기 버튼 클릭 시 모달 닫기
        };

        // 모달 보이기
        modal.style.display = 'block';
    }
}

// 모달 닫기 함수
function closeModal(modal) {
    modal.style.display = 'none';
}

// 보드 수정 함수
function saveBoardChanges(boardId) {
    console.log("dfsdfs" + boards);
    var board = boards.find(b => b.boardId === parseInt(boardId, 10));

    var newTitle = document.getElementById('edit-board-name').value;
    var newContent = document.getElementById('edit-board-description').value;

    if (newTitle.trim() === '') {
        alert('보드 이름을 입력하세요.');
        return;
    }

    // 보드 정보 업데이트
    board.boardName = newTitle;
    board.boardInfo = newContent;

    var updatedBoard = {
        boardName: newTitle,
        boardInfo: newContent
    };

    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    // 서버로 보드 수정 데이터 전송
    $.ajax({
        url: `http://localhost:8080/admin/boards/${boardId}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(updatedBoard),
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function (response) {
            console.log(response);

            console.log(boards);
            // 로컬 리스트 업데이트
            var boardIndex = boards.findIndex(
                b => b.boardId === parseInt(boardId, 10));
            if (boardIndex !== -1) {
                boards[boardIndex].boardName = newTitle;
                boards[boardIndex].boardInfo = newContent;
            }

            // 사이드바 및 칸반보드 업데이트
            redrawSidebar();
            displayBoard(board);

            // 모달 닫기
            closeModal(document.getElementById('edit-board-modal'));

            // 입력 필드 초기화
            document.getElementById('edit-board-error').textContent = '';
        },
        error: function (error) {
            console.error('Error:', error);
            document.getElementById(
                'edit-board-error').textContent = '보드 수정 중 오류가 발생했습니다.';
        }
    });
}

// 보드 삭제 버튼 클릭 시 모달 표시
function confirmDeleteBoard(boardId) {
    var modal = document.getElementById('board-delete-modal');
    modal.style.display = 'block';

    // 확인 버튼 클릭 시
    var confirmButton = document.getElementById('confirm-delete-button');
    confirmButton.onclick = function () {
        deleteBoard(boardId);
    };
}

// 보드 삭제 함수
function deleteBoard(boardId) {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    $.ajax({
        url: `http://localhost:8080/admin/boards/${boardId}`,
        type: 'DELETE',
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function (data) {
            console.log('Board deleted successfully:', data);

            console.log(boardId);
            console.log(typeof boardId);
            // 보드 배열에서 삭제
            var boardIndex = boards.findIndex(
                b => b.boardId === parseInt(boardId, 10));
            console.log(boardIndex);
            if (boardIndex !== -1) {
                boards.splice(boardIndex, 1);

                // 사이드바 다시 그리기
                redrawSidebar();

                // 보드 영역 초기화
                clearKanbanBoard();
            }

            // 삭제 확인 모달 닫기
            hideBoardDeleteModal();
        },
        error: function (err) {
            console.error('Error deleting board:', err);
            alert('보드 삭제 중 오류가 발생했습니다.');
        }
    });
}

// 보드 영역 초기화 함수
function clearKanbanBoard() {
    var kanbanBoard = document.getElementById('kanban-board');
    kanbanBoard.innerHTML = '';
}

// 사이드바 다시 그리기
function redrawSidebar() {
    var boardList = document.getElementById('board-list');
    boardList.innerHTML = '';

    // 보드 배열 확인
    console.log("Boards array:", boards);

    // 보드 배열 순회하여 사이드바에 추가
    boards.forEach(function (board) {
        console.log("Adding board to sidebar:", board);
        addBoardToSidebar(board);
    });
}

// 보드 생성 모달 닫기
function hideEditBoardModal() {
    var modal = document.getElementById('edit-board-modal');
    modal.style.display = 'none';
}

// 컬럼 생성 모달 닫기
function hideColumnEditModal() {
    var modal = document.getElementById('column-creation-modal');
    modal.style.display = 'none';
}

// 보드 삭제 확인 모달 닫기
function hideBoardDeleteModal() {
    var modal = document.getElementById('board-delete-modal');
    modal.style.display = 'none';
}

// 사용자 초대 모달 보이기
function showInviteUserModal(boardId) {
    var modal = document.getElementById('invite-user-modal');
    modal.style.display = 'block';
    modal.dataset.id = boardId;
}

// 사용자 초대 모달 숨기기
function hideInviteUserModal() {
    var modal = document.getElementById('invite-user-modal');
    modal.style.display = 'none';
}

// 사용자 초대 함수
function inviteUser() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    var modal = document.getElementById('invite-user-modal');
    var boardId = modal.dataset.id;
    console.log(boardId);
    var userId = document.getElementById('invite-user-id').value;

    if (userId.trim() === '') {
        alert('사용자 ID를 입력해주세요.');
        return;
    }

    // AJAX 요청
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/admin/boards/' + boardId + '/invitation', true);
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
    xhr.setRequestHeader('Authorization', auth);

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                alert(`${userId}님을 초대했습니다.`);
                hideInviteUserModal();
                // 입력 필드 초기화
                document.getElementById('invite-user-id').value = '';
            } else {
                alert('초대에 실패했습니다. 다시 시도해주세요.');
            }
        }
    };

    var data = JSON.stringify({userName: userId});
    xhr.send(data);
}

// 컬럼 추가 모달 보이기
function showColumnCreationModal(boardId) {
    console.log(boardId);
    var modal = document.getElementById('column-creation-modal');
    modal.style.display = 'block';
    console.log(modal.dataset);
    modal.dataset.id = boardId; // 모달에 보드 제목 데이터 속성으로 저장
}

// 컬럼 추가 모달 닫기
function hideColumnCreationModal() {
    var modal = document.getElementById('column-creation-modal');
    modal.style.display = 'none';
    document.getElementById('column-name').value = '';
}

// 컬럼 생성 함수
function createColumn() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    var modal = document.getElementById('column-creation-modal');
    var boardId = modal.dataset.id;
    var columnName = document.getElementById('column-name').value;
    if (columnName.trim() === '') {
        alert('컬럼 이름을 입력해주세요.');
        return;
    }
    var column = {
        columnName: columnName
    };
    $.ajax({
        url: 'http://localhost:8080/admin/boards/' + boardId + '/columns',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(column),
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function (data) {
            console.log(data.data);
            console.log(data.data.columnId);
            var columnId = data.data.columnId;
            // 보드 찾기
            var board = boards.find(b => b.boardId === parseInt(boardId, 10));
            if (board) {
                // 컬럼 배열이 정의되어 있는지 확인
                if (!board.columns) {
                    board.columns = [];
                }
                // 컬럼 추가
                board.columns.push({
                    columnId: columnId,
                    name: columnName,
                    cards: [] // 컬럼의 카드들을 저장할 배열
                });
                // 모달 닫기
                hideColumnCreationModal();
                // 보드 다시 표시
                displayBoard(board);
            }
        },
        error: function () {
            alert('컬럼 생성에 실패했습니다. 다시 시도해주세요.');
        }
    });
}

// 컬럼 삭제 확인 모달 보이기
function confirmDeleteColumn(columnId, boardId, columnIndex,event) {
    event.stopPropagation(); // 이벤트 전파 중지

    var modal = document.getElementById('column-delete-modal');
    modal.style.display = 'block';

    // 확인 버튼에 삭제 동작 설정
    var confirmButton = document.getElementById('confirm-delete-column-button');

    // 기존 이벤트 리스너 제거
    var newButton = confirmButton.cloneNode(true);
    confirmButton.parentNode.replaceChild(newButton, confirmButton);

    // 삭제 동작 설정
    newButton.addEventListener('click', function () {
        deleteColumn(columnId, boardId,columnIndex);
    });
}

// 컬럼 삭제 함수
function deleteColumn(columnId, boardId, columnIndex) {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    $.ajax({
        url: 'http://localhost:8080/admin/columns/' + columnId,
        type: 'DELETE',
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function() {
            // 보드 찾기
            var board = boards.find(b => b.boardId === boardId);
            if (board && columnIndex !== undefined && columnIndex < board.columns.length) {
                // 컬럼 삭제
                board.columns.splice(columnIndex, 1);

                // 보드 다시 표시
                fetchBoard(boardId);

                // 삭제 확인 모달 닫기
                hideColumnDeleteModal();
            }
        },
        error: function() {
            alert('컬럼 삭제에 실패했습니다. 다시 시도해주세요.');
        }
    });
}

// 컬럼 삭제 확인 모달 닫기
function hideColumnDeleteModal() {
    var modal = document.getElementById('column-delete-modal');
    modal.style.display = 'none';
}

// 카드 추가 모달 보이기
function showCardCreationModal(columnId, boardId) {
    var modal = document.getElementById('card-creation-modal');
    modal.style.display = 'block';
    modal.dataset.columnId = columnId;
    modal.dataset.boardId = boardId;
}

// 카드 추가 모달 닫기
function hideCardCreationModal() {
    var modal = document.getElementById('card-creation-modal');
    modal.style.display = 'none';
}

// 카드 생성 함수
function createCard() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    var modal = document.getElementById('card-creation-modal');
    console.log(modal.dataset);
    console.log(modal.dataset.boardId);
    var boardId = modal.dataset.boardId;
    var columnId = modal.dataset.columnId;
    console.log(modal.dataset.columnId);
    var cardTitle = document.getElementById('card-title').value;
    var cardContent = document.getElementById('card-content').value;
    var cardAssignee = document.getElementById('card-assignee').value;
    var cardDueDate = document.getElementById('card-due-date').value;
    console.log(cardDueDate);
    console.log(typeof cardDueDate);
    var card = {
        title: cardTitle,
        content: cardContent,
        collaborator: cardAssignee,
        deadline: cardDueDate
    };
    if (cardTitle.trim() === '') {
        alert('카드 제목을 입력해주세요.');
        return;
    }
    $.ajax({
        url: `http://localhost:8080/boards/${boardId}/columns/${columnId}/cards`,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(card),
        beforeSend: function (xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function (data) {
            console.log(data.data);
            console.log(data.data.cardId);
            var cardId = data.data.id;
            // 보드 찾기
            var board = boards.find(b => b.boardId === parseInt(boardId, 10));
            if (board) {
                var column = board.columns.find(
                    c => c.columnId === parseInt(columnId, 10));
                if (column) {
                    // 컬럼에 cards 배열이 없으면 생성
                    if (!column.cards) {
                        column.cards = [];
                    }
                    // 새 카드 객체 생성
                    var newCard = {
                        cardId: cardId,
                        title: cardTitle,
                        content: cardContent,
                        collaborator: cardAssignee,
                        deadline: cardDueDate
                    };
                    // 카드 배열에 새 카드 추가
                    column.cards.push(newCard);
                }
                // 모달 닫기
                hideCardCreationModal();
                // 보드 다시 표시
                displayBoard(board);

                document.getElementById('card-title').value = '';
                document.getElementById('card-content').value = '';
                document.getElementById('card-assignee').value = '';
                document.getElementById('card-due-date').value = '';

                console.log(cardId);
            }
        },
        error: function () {
            alert('카드 생성에 실패했습니다. 다시 시도해주세요.');
        }
    });

}

// 카드 수정 모달 열기 함수
function showCardEditModal(boardId, columnIndex, cardIndex, cardId, cardTitle, cardContent, cardAssignee, cardDueDate) {
    var modal = document.getElementById('edit-card-modal');
    var editCardTitleInput = document.getElementById('edit-card-title');
    var editCardContentTextarea = document.getElementById('edit-card-content');
    var editCardAssigneeInput = document.getElementById('edit-card-assignee');
    var editCardDueDateInput = document.getElementById('edit-card-due-date');

    editCardTitleInput.value = cardTitle;
    editCardContentTextarea.value = cardContent;
    editCardAssigneeInput.value = cardAssignee;
    editCardDueDateInput.value = cardDueDate;

    var saveEditCardButton = document.getElementById('save-edit-card-button');
    saveEditCardButton.onclick = function () {
        saveCardEdit(boardId, columnIndex, cardIndex, cardId);
    };

    modal.dataset.boardId = boardId;
    modal.dataset.columnIndex = columnIndex;
    modal.dataset.cardIndex = cardIndex;
    modal.dataset.cardId = cardId;

    modal.style.display = 'block';
}

// 카드 수정 저장 함수
function saveCardEdit(boardId, columnIndex, cardIndex, cardId) {
    const auth = getToken();
    if (!auth) {
        alert('인증 토큰이 없습니다. 다시 로그인해주세요.');
        return;
    }

    var editedTitle = document.getElementById('edit-card-title').value;
    var editedContent = document.getElementById('edit-card-content').value;
    var editedAssignee = document.getElementById('edit-card-assignee').value;
    var editedDueDate = document.getElementById('edit-card-due-date').value;

    var board = boards.find(b => b.boardId === parseInt(boardId, 10));
    if (!board || !board.columns) {
        console.error('Board or columns not found');
        alert('보드 정보를 찾을 수 없습니다.');
        return;
    }

    var column = board.columns[columnIndex];
    if (!column || !column.cards) {
        console.error('Column or cards not found');
        alert('컬럼 정보를 찾을 수 없습니다.');
        return;
    }

    var card = column.cards[cardIndex];
    if (!card) {
        console.error('Card not found');
        alert('카드 정보를 찾을 수 없습니다.');
        return;
    }

    var updatedCard = {
        title: editedTitle,
        content: editedContent,
        collaborator: editedAssignee,
        deadline: editedDueDate
    };

    $.ajax({
        url: `http://localhost:8080/boards/${boardId}/columns/${column.columnId}/cards/${cardId}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(updatedCard),
        beforeSend: function(xhr) {
            xhr.setRequestHeader('Authorization', auth);
        },
        success: function(response) {
            console.log('Card updated successfully:', response);

            // 로컬 데이터 업데이트
            card.title = editedTitle;
            card.content = editedContent;
            card.collaborator = editedAssignee;
            card.deadline = editedDueDate;

            // 보드 다시 표시
            displayBoard(board);

            // 모달 닫기
            hideCardEditModal();
        },
        error: function(xhr, status, error) {
            console.error('Error updating card:', error);
            if (xhr.status === 403) {
                alert('권한이 없습니다. 관리자에게 문의하세요.');
            } else {
                alert('카드 수정에 실패했습니다. 다시 시도해주세요.');
            }
        }
    });
}


document.getElementById('query').addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        searchCards();
    }
});

function displaySearchResults(cards) {
    const searchResultBox = document.getElementById('search-result-box');
    cards.forEach(card => {
        const cardElement = document.createElement('div');
        cardElement.classList.add('card');
        cardElement.innerHTML = `
            <h3>${card.title}</h3>
            <p>${card.content}</p>
            <p>작업자: ${card.collaborator}</p>
            <p>마감일: ${card.deadline}</p>
        `;
        searchResultBox.appendChild(cardElement);
    });
}

//작업자별 조회
function execSearchAssignee() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    const query = document.getElementById('query').value.trim();
    const boardId = getCurrentBoardId(); // 현재 보드 ID를 가져오는 함수
    const searchResultBox = document.getElementById('search-result-box');
    searchResultBox.innerHTML = ''; // 기존 검색 결과 초기화


    fetch(`http://localhost:8080/boards/${boardId}/cards/assignee?collaborator=${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': auth
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('카드 검색 요청 실패');
        }
        return response.json();
    })
    .then(data => {
        displaySearchResults(data.data);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('카드 검색에 실패했습니다. 다시 시도해주세요.');
    });

}

//상태별 조회
function execSearchStatus() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    const query = document.getElementById('query').value.trim();
    const boardId = getCurrentBoardId(); // 현재 보드 ID를 가져오는 함수
    const searchResultBox = document.getElementById('search-result-box');
    searchResultBox.innerHTML = ''; // 기존 검색 결과 초기화

    fetch(`http://localhost:8080/boards/${boardId}/cards/status?columnName=${encodeURIComponent(query)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': auth
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('카드 검색 요청 실패');
        }
        return response.json();
    })
    .then(data => {
        displaySearchResults(data.data);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('카드 검색에 실패했습니다. 다시 시도해주세요.');
    });
}

//전체 조회
function execSearchAll() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }
    const boardId = getCurrentBoardId(); // 현재 보드 ID를 가져오는 함수
    const searchResultBox = document.getElementById('search-result-box');
    searchResultBox.innerHTML = ''; // 기존 검색 결과 초기화

    fetch(`http://localhost:8080/boards/${boardId}/cards`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': auth
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('카드 검색 요청 실패');
        }
        return response.json();
    })
    .then(data => {
        displaySearchResults(data.data);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('카드 검색에 실패했습니다. 다시 시도해주세요.');
    });
}


function getCurrentBoardId() {
    console.log(document.getElementById('card-creation-modal').dataset);
    // 현재 보드 ID를 가져오는 로직
    return document.getElementById('card-creation-modal').dataset.boardId;
}


// 모달 닫기
function hideCardEditModal() {
    var modal = document.getElementById('edit-card-modal');
    modal.style.display = 'none';
}

// 저장된 컬럼들 표시
function displayColumns(board) {
    console.log("Displaying columns for board:", board);
    var columnsContainer = document.getElementById(`columns-${board.boardName}`);
    columnsContainer.innerHTML = '';
    columnsContainer.classList.add('columns');
    board.columns.forEach(function (column, columnIndex) {
        var columnElement = document.createElement('div');
        columnElement.classList.add('column');
        columnElement.id = `column-${board.boardName}-${columnIndex}`;
        var columnNameSpan = document.createElement('span');
        columnNameSpan.textContent = column.columnName;
        columnElement.appendChild(columnNameSpan);
        var deleteColumnButton = document.createElement('button');
        deleteColumnButton.textContent = '컬럼삭제';
        deleteColumnButton.classList.add('delete-column-button');
        deleteColumnButton.onclick = function (event) {
            confirmDeleteColumn(column.columnId, board.boardId, columnIndex, event);
        };
        columnElement.appendChild(deleteColumnButton);
        var addCardButton = document.createElement('button');
        addCardButton.textContent = '카드 추가';
        addCardButton.onclick = function() {
            showCardCreationModal(column.columnId, board.boardId);
        };
        columnElement.appendChild(addCardButton);
        var cardsContainer = document.createElement('div');
        cardsContainer.classList.add('cards');
        if (column.cards && column.cards.length > 0) {
            column.cards.forEach(function (card, cardIndex) {
                var cardElement = document.createElement('div');
                cardElement.classList.add('card');
                cardElement.id = `card-${board.boardId}-${columnIndex}-${cardIndex}`;
                var cardTitleSpan = document.createElement('span');
                cardTitleSpan.textContent = card.title;
                cardTitleSpan.onclick = function () {
                    showCardDetailsModal(card);
                };
                cardElement.appendChild(cardTitleSpan);
                var editCardButton = document.createElement('button');
                editCardButton.textContent = '카드수정';
                editCardButton.classList.add('edit-card-button');
                editCardButton.onclick = function () {
                    showCardEditModal(board.boardId, columnIndex, cardIndex, card.cardId, card.title, card.content, card.collaborator, card.deadline);
                };
                cardElement.appendChild(editCardButton);
                var deleteCardButton = document.createElement('button');
                deleteCardButton.textContent = '카드삭제';
                deleteCardButton.classList.add('delete-card-button');
                deleteCardButton.onclick = function (event) {
                    confirmDeleteCard(board.boardId, columnIndex, cardIndex, event);
                };
                cardElement.appendChild(deleteCardButton);
                cardsContainer.appendChild(cardElement);
            });
        }
        columnElement.appendChild(cardsContainer);
        columnsContainer.appendChild(columnElement);
        initializeSortableCardContainer(columnElement);
    });
    initializeSortableColumns(columnsContainer.closest('.board-content'));
}

// 댓글 등록 함수
function addComment() {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    var cardTitle = document.getElementById('card-details-title').textContent;
    var commentInput = document.getElementById('comment-input').value;

    // 입력값 유효성 검사
    if (commentInput.trim() === '') {
        alert('댓글을 입력해주세요.');
        return;
    }

    // 보드와 카드 찾기
    var board = boards.find(b => b.columns.some(c => c.cards.some(card => card.title === cardTitle)));
    if (board) {
        var column = board.columns.find(col => col.cards.some(card => card.title === cardTitle));
        var card = column.cards.find(card => card.title === cardTitle);

        if (card) {
            var boardId = board.boardId;
            var columnId = column.columnId;
            var cardId = card.cardId;

            var comment = {
                content: commentInput
            };

            $.ajax({
                url: `http://localhost:8080/boards/${boardId}/columns/${columnId}/cards/${cardId}/comments`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(comment),
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', auth);
                },
                success: function(response) {
                    console.log('Server response:', response);
                    if (response && response.data) {
                        var commentData = response.data;
                        var newComment = {
                            userName: commentData.userName,
                            content: commentData.content,
                            createdAt: commentData.createdAt
                        };

                        if (!card.comments) {
                            card.comments = [];
                        }
                        card.comments.unshift(newComment);  // 새 댓글을 배열의 맨 앞에 추가

                        // 새 댓글 추가 후 첫 페이지로 이동
                        currentPage = 1;

                        displayComments(document.getElementById('card-details-modal'), card.comments, card);

                        document.getElementById('comment-input').value = '';
                    } else {
                        console.error('Invalid server response:', response);
                        alert('서버 응답이 올바르지 않습니다.');
                    }
                },
                error: function(xhr, status, error) {
                    console.error('Ajax error:', status, error);
                    alert('댓글 등록에 실패했습니다. 다시 시도해주세요.');
                }
            });
        }
    }
}


// 댓글 표시 함수
function displayComments(container, comments, cardRef) {
    var commentsContainer = container.querySelector('#card-comments-list');
    if (!commentsContainer) {
        commentsContainer = document.createElement('div');
        commentsContainer.id = 'card-comments-list';
        container.querySelector('.modal-content').appendChild(commentsContainer);
    }
    commentsContainer.innerHTML = '';

    // 댓글을 최신순으로 정렬 (가장 최근 댓글이 앞에 오도록)
    comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    // 현재 페이지의 댓글만 선택
    const startIndex = (currentPage - 1) * commentsPerPage;
    const endIndex = startIndex + commentsPerPage;
    const commentsToShow = comments.slice(startIndex, endIndex);

    commentsToShow.forEach(function (comment) {
        var commentElement = document.createElement('div');
        commentElement.classList.add('comment');
        commentElement.innerHTML = `
      <p>${comment.content}</p>
      <span class="comment-username">${comment.userName}</span>
      <span class="comment-timestamp">${new Date(comment.createdAt).toLocaleString()}</span>
    `;
        commentsContainer.appendChild(commentElement);
    });

    // 페이지네이션 컨트롤 추가
    addPaginationControls(container, comments.length, cardRef);
}
//페이지네이션 컨트롤 함수
function addPaginationControls(container, totalComments, cardRef) {
    const totalPages = Math.ceil(totalComments / commentsPerPage);

    let paginationContainer = container.querySelector('#pagination-controls');
    if (!paginationContainer) {
        paginationContainer = document.createElement('div');
        paginationContainer.id = 'pagination-controls';
        container.querySelector('.modal-content').appendChild(paginationContainer);
    }
    paginationContainer.innerHTML = '';

    // 이전 페이지 버튼
    if (currentPage > 1) {
        const prevButton = document.createElement('button');
        prevButton.textContent = '이전';
        prevButton.onclick = () => {
            currentPage--;
            displayComments(container, cardRef.comments, cardRef);
        };
        paginationContainer.appendChild(prevButton);
    }

    // 페이지 번호
    const pageInfo = document.createElement('span');
    pageInfo.textContent = `${currentPage} / ${totalPages}`;
    paginationContainer.appendChild(pageInfo);

    // 다음 페이지 버튼
    if (currentPage < totalPages) {
        const nextButton = document.createElement('button');
        nextButton.textContent = '다음';
        nextButton.onclick = () => {
            currentPage++;
            displayComments(container, cardRef.comments, cardRef);
        };
        paginationContainer.appendChild(nextButton);
    }
}

// 카드 상세 정보 모달 보이기
function showCardDetailsModal(card) {
    var modal = document.getElementById('card-details-modal');
    modal.style.display = 'block';

    // 모달 내용 설정
    document.getElementById('card-details-title').textContent = card.title;
    document.getElementById('card-details-content').textContent = card.content;
    document.getElementById('card-details-assignee').textContent = card.assignee;
    document.getElementById('card-details-due-date').textContent = card.dueDate;

    // 댓글 표시
    displayComments(modal, card.comments || [], card);

    // 모달 닫기 버튼 이벤트 설정
    var closeButton = document.getElementById('card-details-close-button');
    closeButton.onclick = function () {
        hideCardDetailsModal();
    };

    // 댓글 등록 버튼 이벤트 설정
    var commentButton = document.getElementById('comment-button');
    commentButton.onclick = addComment;
}

// 카드 상세 정보 모달 닫기
function hideCardDetailsModal() {
    var modal = document.getElementById('card-details-modal');
    modal.style.display = 'none';
}

// 카드 삭제 확인 모달 보이기
function confirmDeleteCard(boardId, columnIndex, cardIndex, event) {
    event.stopPropagation(); // 이벤트 전파 중지

    var modal = document.getElementById('card-delete-modal');
    modal.style.display = 'block';

    // 확인 버튼에 삭제 동작 설정
    var confirmButton = document.getElementById('confirm-delete-card-button');

    // 기존 이벤트 리스너 제거
    var newButton = confirmButton.cloneNode(true);
    confirmButton.parentNode.replaceChild(newButton, confirmButton);

    // 삭제 동작 설정
    newButton.addEventListener('click', function () {
        deleteCard(boardId, columnIndex, cardIndex);
    });
}

// 카드 삭제 함수
function deleteCard(boardId, columnIndex, cardIndex) {
    const auth = getToken();
    if (!auth) {
        window.location.href = '/users/login-page';
        return;
    }

    // 보드 찾기
    var board = boards.find(b => b.boardId === parseInt(boardId, 10));
    var columnId = board.columns[columnIndex].columnId;
    var cardId = board.columns[columnIndex].cards[cardIndex].cardId;

    if (board && columnIndex !== undefined && cardIndex !== undefined) {
        // AJAX 요청 보내기
        var xhr = new XMLHttpRequest();
        xhr.open('DELETE',
            `/boards/${boardId}/columns/${columnId}/cards/${cardId}`);
        xhr.setRequestHeader('Authorization', auth);

        xhr.onreadystatechange = function () {
            if (xhr.status === 200) {

                // 카드 삭제 성공
                board.columns[columnIndex].cards.splice(cardIndex, 1);
                // 다시 보드 표시
                displayBoard(board);
                // 삭제 확인 모달 닫기
                hideCardDeleteModal();
            } else {
                // 카드 삭제 실패
                alert('카드 삭제에 실패했습니다.');
            }
        };
        xhr.send();
    }
}

// 카드 삭제 확인 모달 닫기
function hideCardDeleteModal() {
    var modal = document.getElementById('card-delete-modal');
    modal.style.display = 'none';
}

// 컬럼 수정 모달 열기 함수
function showColumnEditModal(boardTitle, columnIndex, currentColumnName) {
    var modal = document.getElementById('edit-column-modal');
    var editColumnNameInput = document.getElementById('edit-column-name');
    var saveColumnButton = document.getElementById('save-column-button');
    var cancelEditColumnButton = document.getElementById(
        'cancel-edit-column-button');

    // 현재 컬럼 이름을 입력 필드에 설정
    editColumnNameInput.value = currentColumnName;

    // 저장 버튼 클릭 시 처리할 이벤트 설정
    saveColumnButton.onclick = function () {
        var newColumnName = editColumnNameInput.value.trim();

        // 유효성 검사: 이름이 비어있지 않아야 함
        if (newColumnName === '') {
            alert('컬럼 이름을 입력하세요.');
            return;
        }

        // 보드 및 컬럼 인덱스, 새로운 컬럼 이름을 전달하여 컬럼 이름 업데이트
        updateColumnName(boardTitle, columnIndex, newColumnName);

        // 모달 닫기
        closeModal(modal);
    };

    // 취소 버튼 클릭 시 모달 닫기
    cancelEditColumnButton.onclick = function () {
        closeModal(modal);
    };

    // 모달 표시
    modal.style.display = 'block';
}

// 컬럼 이름 업데이트 함수
function updateColumnName(boardTitle, columnIndex, newColumnName) {
    var board = boards.find(b => b.title === boardTitle);
    if (board && board.columns && board.columns[columnIndex]) {
        board.columns[columnIndex].name = newColumnName;
        displayColumns(board); // 변경된 컬럼 표시
    }
}

// 카드 순서 변경
function initializeSortableCardContainer(column) {
    new Sortable(column.querySelector(".cards"), {
        group: "cards",
        animation: 150,
        ghostClass: "blue-background-class",
        onEnd: async function (evt) {
            const oldIndex = evt.oldIndex;
            const newIndex = evt.newIndex;
            const newColumn = evt.to.closest('.column');
            const newColumnIndex = Array.from(
                newColumn.parentNode.children).indexOf(
                newColumn);
            const oldColumn = evt.from.closest('.column');
            const oldColumnIndex = Array.from(
                oldColumn.parentNode.children).indexOf(
                oldColumn);
            const boardTitle = column.closest('.board-content').querySelector(
                'h2').textContent;
            const board = boards.find(b => b.boardName === boardTitle);

            if (board) {
                var boardId = board.boardId;
                var oldColumnId = board.columns[oldColumnIndex].columnId;
                var newColumnId = board.columns[newColumnIndex].columnId;
                const movedCard = board.columns[oldColumnIndex].cards.splice(oldIndex,
                    1)[0];
                var cardId = movedCard.cardId;
                board.columns[newColumnIndex].cards.splice(newIndex, 0, movedCard);

                // 백엔드로 카드 이동 요청 보내기
                try {
                    console.log(
                        `Sending request to move card: ${cardId} to index: ${newIndex} in column: ${newColumnId}`);
                    const response = await fetch(
                        `/boards/${boardId}/columns/${oldColumnId}/cards/${cardId}/transfer`,
                        {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': getToken() // JWT 토큰 추가
                            },
                            body: JSON.stringify({
                                columnId: newColumnId,
                                cardIndex: newIndex
                            }),
                        });

                    if (!response.ok) {
                        throw new Error('카드 이동 요청 실패');
                    }

                    const result = await response.json();
                    console.log(result.message); // 카드 이동되었습니다.
                } catch (error) {
                    console.error('Error:', error);
                    // 여기서 오류 메시지를 사용자에게 보여줄 수 있습니다.
                }
            }
        },
    });
}

// 컬럼 순서변경
function initializeSortableColumns(boardContent) {
    new Sortable(boardContent.querySelector(".columns"), {
        group: "columns",
        animation: 150,
        handle: ".column > span", // 컬럼 이름을 드래그 핸들로 사용
        ghostClass: "blue-background-class",
        onEnd: async function (evt) {
            const oldIndex = evt.oldIndex;
            const newIndex = evt.newIndex;
            const boardTitle = boardContent.querySelector('h2').textContent;
            const board = boards.find(b => b.boardName === boardTitle);

            if (board) {
                var boardId = board.boardId;
                const movedColumn = board.columns.splice(oldIndex, 1)[0];
                var columnId = movedColumn.columnId;
                board.columns.splice(newIndex, 0, movedColumn);
                displayColumns(board);
                // 변경 사항 반영하여 다시 그리기

                // 백엔드로 컬럼 이동 요청 보내기
                try {

                    console.log(
                        `Sending request to move column: ${columnId} to index: ${newIndex}, boardId : ${boardId}`);
                    const response = await fetch(
                        `/admin/boards/${boardId}/columns/${columnId}/transfer`, {
                            method: 'PUT',

                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': auth
                            },

                            body: JSON.stringify({
                                columnIndex: newIndex
                            }),
                        });

                    if (!response.ok) {
                        throw new Error('컬럼 이동 요청 실패');
                    }

                    const result = await response.json();
                    console.log(result.message); // 컬럼 이동되었습니다.
                } catch (error) {
                    console.error('Error:', error);
                    // 여기서 오류 메시지를 사용자에게 보여줄 수 있습니다.
                }
            }
        },
    });
}

// 모든 컬럼과 보드에 Sortable 초기화
function initializeSortable() {
    document.querySelectorAll(".column").forEach((column) => {
        initializeSortableCardContainer(column);
    });

    const boards = document.querySelectorAll(".board-content");
    boards.forEach((board) => {
        initializeSortableColumns(board);
    });
}

// // 초기화 함수 호출
// document.addEventListener('DOMContentLoaded', () => {
//     initializeSortable();
// });
//
//

// function execSearch() {
//   /**
//    * 검색어 input id: query
//    * 검색결과 목록: #search-result-box
//    * 검색결과 HTML 만드는 함수: addHTML
//    */
//       // 1. 검색창의 입력값을 가져온다.
//   let query = $('#query').val();
//
//   // 2. 검색창 입력값을 검사하고, 입력하지 않았을 경우 focus.
//   if (query == '') {
//     alert('검색어를 입력해주세요');
//     $('#query').focus();
//     return;
//   }
// 3. GET /api/search?query=${query} 요청
// $.ajax({
//     type: 'GET',
//     url: `/api/search?query=${query}`,
//     success: function (response) {
//         $('#search-result-box').empty();
//         // 4. for 문마다 itemDto를 꺼내서 HTML 만들고 검색결과 목록에 붙이기!
//         for (let i = 0; i < response.length; i++) {
//             let itemDto = response[i];
//             let tempHtml = addHTML(itemDto);
//             $('#search-result-box').append(tempHtml);
//         }
//     },
//     error(error, status, request) {
//         logout();
//     }
// })

// }