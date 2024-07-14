var boards = []; // 배열 초기화


$(document).ready(function () {
  const auth = getToken();
  if (!auth) {
    window.location.href = '/users/login-page';
    return;
  }

  $.ajax({
    url: 'http://localhost:8080/boards',
    type: 'GET',
    beforeSend: function(xhr) {
      xhr.setRequestHeader('Authorization', auth);
    },
    success: function(data) {
      console.log(data);
      boards = data.data; // 서버에서 받은 데이터를 boards에 할당
      console.log(boards); // boards 배열 확인
      boards.forEach(function(board) {
        addBoardToSidebar(board);
      });
    },
    error: function(error) {
      console.error('Error:', error);
    }
  });

  // id 가 query 인 녀석 위에서 엔터를 누르면 execSearch() 함수를 실행하라는 뜻입니다.
  $('#query').on('keypress', function (e) {
    if (e.key == 'Enter') {
      execSearch();
    }
  });

  $('#kanban-board').show();
  $('#explore-section').hide();

  $('.nav div.nav-see').on('click', function () {
    $('div.nav-see').addClass('active');
    $('div.nav-search').removeClass('active');

    $('#kanban-board').show();
    $('#explore-section').hide();
  })
  $('.nav div.nav-search').on('click', function () {
    $('div.nav-see').removeClass('active');
    $('div.nav-search').addClass('active');

    $('#kanban-board').hide();
    $('#explore-section').show();
  })

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

  if(auth === undefined) {
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
    beforeSend: function(xhr) {
      xhr.setRequestHeader('Authorization', auth);
    },
    success: function(data) {
      console.log(data);
      var boardId = data.data.boardId;
      boards.push({ ...board, boardId });
      addBoardToSidebar({ ...board, boardId });
      hideBoardCreationModal();
      document.getElementById('board-title').value = '';
      document.getElementById('board-content').value = '';
      document.getElementById('board-error').textContent = '';
    },
    error: function() {
      document.getElementById('board-error').textContent = '보드 생성 중 오류가 발생했습니다.';
    }
  });
}

// 사이드바에 보드 이름 추가
function addBoardToSidebar(board) {
  console.log(board); // 보드 ID 출력
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

  boardItem.addEventListener('click', function() {
    displayBoard(board);
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
            <button onclick="showColumnCreationModal('${board.boardName}')">컬럼 추가</button>
        </div>
    `;
  // 저장된 컬럼들 표시
  displayColumns(board);
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
    saveButton.onclick = function() {
      saveBoardChanges(boardId);
      closeModal(modal); // 저장 후 모달 닫기
    };

    // 취소 버튼에 클릭 이벤트 리스너 추가
    var cancelButton = document.getElementById('cancel-edit-button');
    cancelButton.onclick = function() {
      closeModal(modal); // 취소 시 모달 닫기
    };

    // 모달 닫기 버튼에 클릭 이벤트 리스너 추가
    var closeButton = modal.querySelector('.close');
    closeButton.onclick = function() {
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
  console.log("dfsdfs"+boards);
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
    beforeSend: function(xhr) {
      xhr.setRequestHeader('Authorization', auth);
    },
    success: function(response) {
      console.log(response);

      console.log(boards);
      // 로컬 리스트 업데이트
      var boardIndex = boards.findIndex(b => b.boardId === parseInt(boardId, 10));
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
    error: function(error) {
      console.error('Error:', error);
      document.getElementById('edit-board-error').textContent = '보드 수정 중 오류가 발생했습니다.';
    }
  });
}

// 보드 삭제 버튼 클릭 시 모달 표시
function confirmDeleteBoard(boardId) {
  var modal = document.getElementById('board-delete-modal');
  modal.style.display = 'block';

  // 확인 버튼 클릭 시
  var confirmButton = document.getElementById('confirm-delete-button');
  confirmButton.onclick = function() {
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
    beforeSend: function(xhr) {
      xhr.setRequestHeader('Authorization', auth);
    },
    success: function(data) {
      console.log('Board deleted successfully:', data);

      console.log(boardId);
      console.log(typeof boardId);
      // 보드 배열에서 삭제
      var boardIndex = boards.findIndex(b => b.boardId === parseInt(boardId, 10));
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
    error: function(err) {
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
  boards.forEach(function(board) {
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

  var data = JSON.stringify({ userName: userId });
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
  console.log(modal.dataset);
  console.log(boardId);
  var columnName = document.getElementById('column-name').value;

  if (columnName.trim() === '') {
    alert('컬럼 이름을 입력해주세요.');
    return;
  }

  // AJAX 요청
  var xhr = new XMLHttpRequest();
  xhr.open('POST', '/admin/boards/' + boardId + '/columns', true);
  xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
  xhr.setRequestHeader('Authorization', auth);

  xhr.onreadystatechange = function () {
    if (xhr.readyState === XMLHttpRequest.DONE) {
      if (xhr.status === 200) {
        // 보드 찾기
        var board = boards.find(b => b.boardId === parseInt(boardId, 10));
        console.log(board);
        if (board) {
          // 컬럼 배열이 정의되어 있는지 확인
          if (!board.columns) {
            board.columns = [];
          }
          // 컬럼 추가
          board.columns.push({
            name: columnName,
            cards: [] // 컬럼의 카드들을 저장할 배열
          });

          // 모달 닫기
          hideColumnCreationModal();

          // 보드 다시 표시
          displayBoard(board);
        }
      } else {
        alert('컬럼 생성에 실패했습니다. 다시 시도해주세요.');
      }
    }
  };

  var data = JSON.stringify({ columnName: columnName });
  xhr.send(data);
}

// 컬럼 삭제 확인 모달 보이기
function confirmDeleteColumn(boardTitle, columnIndex, event) {
  event.stopPropagation(); // 이벤트 전파 중지

  var modal = document.getElementById('column-delete-modal');
  modal.style.display = 'block';

  // 확인 버튼에 삭제 동작 설정
  var confirmButton = document.getElementById('confirm-delete-column-button');

  // 기존 이벤트 리스너 제거
  var newButton = confirmButton.cloneNode(true);
  confirmButton.parentNode.replaceChild(newButton, confirmButton);

  // 삭제 동작 설정
  newButton.addEventListener('click', function() {
    deleteColumn(boardTitle, columnIndex);
  });
}

// 컬럼 삭제 함수
function deleteColumn(boardTitle, columnIndex) {
  // 보드 찾기
  var board = boards.find(b => b.boardName === boardTitle);
  if (board && columnIndex !== undefined && columnIndex < board.columns.length) {
    // 컬럼 삭제
    board.columns.splice(columnIndex, 1);

    // 보드 다시 표시
    displayBoard(board);

    // 삭제 확인 모달 닫기
    hideColumnDeleteModal();
  }
}

// 컬럼 삭제 확인 모달 닫기
function hideColumnDeleteModal() {
  var modal = document.getElementById('column-delete-modal');
  modal.style.display = 'none';
}

// 카드 추가 모달 보이기
function showCardCreationModal(columnIndex, boardTitle) {
  var modal = document.getElementById('card-creation-modal');
  modal.style.display = 'block';
  modal.dataset.columnIndex = columnIndex;
  modal.dataset.boardTitle = boardTitle;
}

// 카드 추가 모달 닫기
function hideCardCreationModal() {
  var modal = document.getElementById('card-creation-modal');
  modal.style.display = 'none';
}

// 카드 생성 함수
function createCard() {
  var boardTitle = document.getElementById('card-creation-modal').dataset.boardTitle;
  var columnIndex = document.getElementById('card-creation-modal').dataset.columnIndex;
  var cardTitle = document.getElementById('card-title').value;
  var cardContent = document.getElementById('card-content').value;
  var cardAssignee = document.getElementById('card-assignee').value;
  var cardDueDate = document.getElementById('card-due-date').value;

  if (cardTitle.trim() === '') {
    alert('카드 제목을 입력해주세요.');
    return;
  }

  // 보드와 컬럼 찾기
  var board = boards.find(b => b.title === boardTitle);
  if (board && columnIndex !== undefined && columnIndex < board.columns.length) {
    // 카드 추가
    var card = {
      title: cardTitle,
      content: cardContent,
      assignee: cardAssignee,
      dueDate: cardDueDate,
      comments: [] // 카드의 댓글들을 저장할 배열
    };
    board.columns[columnIndex].cards.push(card);

    // 모달 닫기
    hideCardCreationModal();

    // 보드 다시 표시
    displayColumns(board);
  }
}

// 카드 수정 모달 열기 함수
function showCardEditModal(boardTitle, columnIndex, cardIndex, cardTitle, cardContent, cardAssignee, cardDueDate) {
  // 기존에 열려 있는 모든 모달 닫기
  document.querySelectorAll('.modal').forEach(function(modal) {
    modal.style.display = 'none';
  });

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
  saveEditCardButton.onclick = function() {
    saveCardEdit(boardTitle, columnIndex, cardIndex);
    hideCardEditModal();
  };

  modal.style.display = 'block';
}

// 카드 수정 저장 함수
function saveCardEdit(boardTitle, columnIndex, cardIndex) {
  var editedTitle = document.getElementById('edit-card-title').value;
  var editedContent = document.getElementById('edit-card-content').value;
  var editedAssignee = document.getElementById('edit-card-assignee').value;
  var editedDueDate = document.getElementById('edit-card-due-date').value;

  // 실제로 데이터를 업데이트하고 화면을 다시 그리는 작업이 필요합니다.
  var board = boards.find(b => b.title === boardTitle);
  if (board) {
    var cardToUpdate = board.columns[columnIndex].cards[cardIndex];
    cardToUpdate.title = editedTitle;
    cardToUpdate.content = editedContent;
    cardToUpdate.assignee = editedAssignee;
    cardToUpdate.dueDate = editedDueDate;

    // 화면 다시 그리기
    displayColumns(board);
  }
}

// 모달 닫기
function hideCardEditModal() {
  var modal = document.getElementById('edit-card-modal');
  modal.style.display = 'none';
}

// 저장된 컬럼들 표시
function displayColumns(board) {
  var columnsContainer = document.getElementById(`columns-${board.boardName}`);
  columnsContainer.innerHTML = ''; // 초기화

  board.columns.forEach(function(column, columnIndex) {
    var columnElement = document.createElement('div');
    columnElement.classList.add('column');

    // 컬럼 제목 출력
    var columnNameSpan = document.createElement('span');
    columnNameSpan.textContent = column.name;
    columnElement.appendChild(columnNameSpan);

    // 삭제 버튼 추가 (컬럼 삭제)
    var deleteColumnButton = document.createElement('button');
    deleteColumnButton.textContent = '컬럼삭제';
    deleteColumnButton.classList.add('delete-column-button');
    deleteColumnButton.onclick = function(event) {
      confirmDeleteColumn(board.title, columnIndex, event);
    };
    columnElement.appendChild(deleteColumnButton);

    // 카드 추가 버튼
    var addCardButton = document.createElement('button');
    addCardButton.textContent = '카드 추가';
    addCardButton.onclick = function() {
      showCardCreationModal(columnIndex, board.title);
    };
    columnElement.appendChild(addCardButton);

    // 카드 표시
    column.cards.forEach(function(card, cardIndex) {
      var cardElement = document.createElement('div');
      cardElement.classList.add('card');

      // 카드 제목 출력
      var cardTitleSpan = document.createElement('span');
      cardTitleSpan.textContent = card.title;
      cardTitleSpan.onclick = function() {
        showCardDetailsModal(card); // 카드 제목 클릭 시 상세 정보 모달 표시
      };
      cardElement.appendChild(cardTitleSpan);

      // 수정 버튼 추가 (카드 수정)
      var editCardButton = document.createElement('button');
      editCardButton.textContent = '카드수정';
      editCardButton.classList.add('edit-card-button');
      editCardButton.onclick = function() {
        showCardEditModal(board.title, columnIndex, cardIndex, card.title, card.content, card.assignee, card.dueDate);
      };
      cardElement.appendChild(editCardButton);

      // 삭제 버튼 추가 (카드 삭제)
      var deleteCardButton = document.createElement('button');
      deleteCardButton.textContent = '카드삭제';
      deleteCardButton.classList.add('delete-card-button');
      deleteCardButton.onclick = function(event) {
        confirmDeleteCard(board.title, columnIndex, cardIndex, event);
      };
      cardElement.appendChild(deleteCardButton);

      columnElement.appendChild(cardElement);
    });

    columnsContainer.appendChild(columnElement);
  });
}

// 댓글 등록 함수
function addComment() {
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
    var card = board.columns.reduce((found, col) => found ? found : col.cards.find(card => card.title === cardTitle), null);
    if (card) {
      // 댓글 객체 생성
      var comment = {
        text: commentInput,
        timestamp: new Date().toLocaleString()
      };

      // 댓글 추가
      card.comments.push(comment);

      // 댓글 목록 업데이트
      displayComments(document.getElementById('card-details-modal'), card.comments);

      // 입력 필드 초기화
      document.getElementById('comment-input').value = '';
    }
  }
}

// 댓글 표시 함수
function displayComments(container, comments) {
  var commentsContainer = container.querySelector('#card-comments-list');
  if (!commentsContainer) {
    commentsContainer = document.createElement('div');
    commentsContainer.id = 'card-comments-list';
    container.querySelector('.modal-content').appendChild(commentsContainer);
  } else {
    commentsContainer.innerHTML = '';
  }

  comments.forEach(function(comment) {
    var commentElement = document.createElement('div');
    commentElement.classList.add('comment');
    commentElement.innerHTML = `
            <p>${comment.text}</p>
            <span class="comment-timestamp">${comment.timestamp}</span>
        `;
    commentsContainer.appendChild(commentElement);
  });
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
  displayComments(modal, card.comments);

  // 모달 닫기 버튼 이벤트 설정
  var closeButton = document.getElementById('card-details-close-button');
  closeButton.onclick = function() {
    hideCardDetailsModal();
  };

  // 댓글 등록 버튼 이벤트 설정
  var commentButton = document.getElementById('comment-button');
  commentButton.onclick = function() {
    addComment();
  };
}

// 카드 상세 정보 모달 닫기
function hideCardDetailsModal() {
  var modal = document.getElementById('card-details-modal');
  modal.style.display = 'none';
}

// 카드 삭제 확인 모달 보이기
function confirmDeleteCard(boardTitle, columnIndex, cardIndex) {
  event.stopPropagation(); // 이벤트 전파 중지

  var modal = document.getElementById('card-delete-modal');
  modal.style.display = 'block';

  // 확인 버튼에 삭제 동작 설정
  var confirmButton = document.getElementById('confirm-delete-card-button');

  // 기존 이벤트 리스너 제거
  var newButton = confirmButton.cloneNode(true);
  confirmButton.parentNode.replaceChild(newButton, confirmButton);

  // 삭제 동작 설정
  newButton.addEventListener('click', function() {
    deleteCard(boardTitle, columnIndex, cardIndex);
  });
}

// 카드 삭제 함수
function deleteCard(boardTitle, columnIndex, cardIndex) {
  // 보드 찾기
  var board = boards.find(b => b.title === boardTitle);
  if (board && columnIndex !== undefined && columnIndex < board.columns.length && cardIndex !== undefined && cardIndex < board.columns[columnIndex].cards.length) {
    // 카드 삭제
    board.columns[columnIndex].cards.splice(cardIndex, 1);

    // 다시 보드 표시
    displayColumns(board);

    // 삭제 확인 모달 닫기
    hideCardDeleteModal();
  }
}

// 카드 삭제 확인 모달 닫기
function hideCardDeleteModal() {
  var modal = document.getElementById('card-delete-modal');
  modal.style.display = 'none';
}

function execSearch() {
  /**
   * 검색어 input id: query
   * 검색결과 목록: #search-result-box
   * 검색결과 HTML 만드는 함수: addHTML
   */
      // 1. 검색창의 입력값을 가져온다.
  let query = $('#query').val();

  // 2. 검색창 입력값을 검사하고, 입력하지 않았을 경우 focus.
  if (query == '') {
    alert('검색어를 입력해주세요');
    $('#query').focus();
    return;
  }
  // // 3. GET /api/search?query=${query} 요청
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

}