// 보드 목록 데이터
var boards = [];

// 초기화 함수
function initialize() {
  // 보드 생성 버튼 이벤트 리스너 등록
  document.getElementById('board-create-button').addEventListener('click', showBoardCreationModal);
  // 모달 닫기 버튼 이벤트 리스너 등록
  document.querySelectorAll('.modal .close').forEach(function(closeBtn) {
    closeBtn.addEventListener('click', function() {
      closeBtn.closest('.modal').style.display = 'none';
    });
  });
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

  // 보드 생성 처리
  var board = {
    title: title,
    content: content,
    columns: [] // 보드의 컬럼들을 저장할 배열
  };
  boards.push(board);

  // 사이드바에 보드 이름 추가
  addBoardToSidebar(board);

  // 모달 닫기
  hideBoardCreationModal();

  // 입력 필드 초기화
  document.getElementById('board-title').value = '';
  document.getElementById('board-content').value = '';
  document.getElementById('board-error').textContent = '';
}

// 사이드바에 보드 이름 추가
function addBoardToSidebar(board) {
  var boardList = document.getElementById('board-list');

  var boardItem = document.createElement('div');
  boardItem.classList.add('board-item');
  // 보드 제목과 수정 및 삭제 버튼 추가
  boardItem.innerHTML = `
        <span>${board.title}</span>
        <button class="edit-board-button" onclick="openEditBoardModal('${board.title}')">보드 수정</button>
        <button class="delete-board-button" onclick="confirmDeleteBoard('${board.title}')">보드 삭제</button>
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
            <h2>${board.title}</h2>
            <p>${board.content}</p>
            <div class="columns" id="columns-${board.title}">
                <!-- 컬럼들이 여기에 추가됩니다 -->
            </div>
            <button onclick="showColumnCreationModal('${board.title}')">컬럼 추가</button>
        </div>
    `;
  // 저장된 컬럼들 표시
  displayColumns(board);

  // 컬럼 드래그 앤 드롭 활성화
  new Sortable(document.getElementById(`columns-${board.title}`), {
    group: 'shared',
    animation: 150,
    ghostClass: 'sortable-ghost',
    onEnd: function (evt) {
      var movedColumn = board.columns.splice(evt.oldIndex, 1)[0];
      board.columns.splice(evt.newIndex, 0, movedColumn);
      console.log('Column moved from index', evt.oldIndex, 'to', evt.newIndex);
    },
  });
}

// 보드 수정 모달 열기 함수
function showEditBoardModal(boardTitle) {
  var modal = document.getElementById('edit-board-modal');
  var board = boards.find(b => b.title === boardTitle);

  if (board) {
    document.getElementById('edit-board-name').value = board.title;
    document.getElementById('edit-board-description').value = board.content;

    // 저장 버튼에 클릭 이벤트 리스너 추가
    var saveButton = document.getElementById('save-board-button');
    saveButton.onclick = function() {
      saveBoardChanges(board);
      hideEditBoardModal(); // 저장 후 모달 닫기
    };

    // 취소 버튼에 클릭 이벤트 리스너 추가
    var cancelButton = document.getElementById('cancel-edit-button');
    cancelButton.onclick = function() {
      closeModal(modal); // 취소 시 모달 닫기
    };

    // 모달 닫기 버튼에 클릭 이벤트 리스너 추가
    var closeButton = modal.querySelector('.close');
    closeButton.addEventListener('click', function() {
      closeModal(modal);
    });

    // 모달 보이기
    modal.style.display = 'block';

    // 이벤트 리스너가 한 번만 추가되어야 하므로, 다시 추가하지 않습니다.
  }
}


// 모달 닫기 함수
function closeModal(modal) {
  modal.style.display = 'none';
}


// 보드 수정 내용 저장 함수
function saveBoardChanges(board) {
  var newTitle = document.getElementById('edit-board-name').value;
  var newContent = document.getElementById('edit-board-description').value;

  if (newTitle.trim() === '') {
    alert('보드 이름을 입력하세요.');
    return;
  }

  // 보드 정보 업데이트
  board.title = newTitle;
  board.content = newContent;

  // 사이드바 및 칸반보드 업데이트
  redrawSidebar();
  displayBoard(board);

  // 모달 닫기
  closeModal(document.getElementById('edit-board-modal'));
}

// 모달 닫기 함수
function closeModal(modal) {
  modal.style.display = 'none';
}

// 보드 삭제 버튼 클릭 시 모달 표시
function confirmDeleteBoard(boardTitle) {
  var modal = document.getElementById('board-delete-modal');
  modal.style.display = 'block';

  // 확인 버튼 클릭 시
  var confirmButton = document.getElementById('confirm-delete-button');
  confirmButton.onclick = function() {
    deleteBoard(boardTitle);
  };
}

// 보드 삭제 함수
function deleteBoard(boardTitle) {
  // 보드 찾기
  var boardIndex = boards.findIndex(b => b.title === boardTitle);
  if (boardIndex !== -1) {
    // 보드 삭제
    boards.splice(boardIndex, 1);

    // 사이드바 다시 그리기
    redrawSidebar();

    // 삭제 확인 모달 닫기
    hideBoardDeleteModal();

    // 보드 영역 초기화
    clearKanbanBoard();
  }
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

  // 모든 보드 다시 추가
  boards.forEach(function(board) {
    addBoardToSidebar(board);
  });
}

// 보드 삭제 확인 모달 닫기
function hideBoardDeleteModal() {
  var modal = document.getElementById('board-delete-modal');
  modal.style.display = 'none';
}

// 컬럼 추가 모달 보이기
function showColumnCreationModal(boardTitle) {
  var modal = document.getElementById('column-creation-modal');
  modal.style.display = 'block';
  modal.dataset.boardTitle = boardTitle; // 모달에 보드 제목 데이터 속성으로 저장
}

// 컬럼 추가 모달 닫기
function hideColumnCreationModal() {
  var modal = document.getElementById('column-creation-modal');
  modal.style.display = 'none';
}

// 컬럼 생성 함수
function createColumn() {
  var boardTitle = document.getElementById('column-creation-modal').dataset.boardTitle;
  var columnName = document.getElementById('column-name').value;

  if (columnName.trim() === '') {
    alert('컬럼 이름을 입력해주세요.');
    return;
  }

  // 보드 찾기
  var board = boards.find(b => b.title === boardTitle);
  if (board) {
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
  var board = boards.find(b => b.title === boardTitle);
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

function showCardEditModal(boardTitle, columnIndex, cardIndex, cardTitle, cardContent) {
  // 카드 수정 모달 표시 코드
  var editCardTitleInput = document.getElementById('edit-card-title');
  var editCardContentTextarea = document.getElementById('edit-card-content');

  editCardTitleInput.value = cardTitle;
  editCardContentTextarea.value = cardContent;

  var saveEditCardButton = document.getElementById('save-edit-card-button');
  saveEditCardButton.onclick = function() {
    // 수정된 카드 제목과 내용으로 업데이트
    var newCardTitle = editCardTitleInput.value;
    var newCardContent = editCardContentTextarea.value;
    editCard(boardTitle, columnIndex, cardIndex, newCardTitle, newCardContent);
    hideCardEditModal();
  };
}

function editCard(boardTitle, columnIndex, cardIndex, newTitle, newContent) {
  var board = boards.find(b => b.title === boardTitle);
  if (board && columnIndex !== undefined && cardIndex !== undefined && columnIndex < board.columns.length && cardIndex < board.columns[columnIndex].cards.length) {
    var card = board.columns[columnIndex].cards[cardIndex];
    card.title = newTitle;
    card.content = newContent;

    // 다시 보드 표시
    displayColumns(board);
  }
}


// 저장된 컬럼들 표시
function displayColumns(board) {
  var columnsContainer = document.getElementById(`columns-${board.title}`);
  columnsContainer.innerHTML = ''; // 초기화

  board.columns.forEach(function(column, columnIndex) {
    var columnElement = document.createElement('div');
    columnElement.classList.add('column');

    // 컬럼 제목 출력
    var columnNameSpan = document.createElement('span');
    columnNameSpan.textContent = column.name;
    columnElement.appendChild(columnNameSpan);

    // 수정 버튼 추가 (컬럼 수정)
    var editColumnButton = document.createElement('button');
    editColumnButton.textContent = '컬럼수정';
    editColumnButton.classList.add('edit-column-button');
    editColumnButton.onclick = function() {
      showColumnEditModal(board.title, columnIndex, column.name);
    };
    columnElement.appendChild(editColumnButton);

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
      cardElement.appendChild(cardTitleSpan);

      // 수정 버튼 추가 (카드 수정)
      var editCardButton = document.createElement('button');
      editCardButton.textContent = '카드수정';
      editCardButton.classList.add('edit-card-button');
      editCardButton.onclick = function() {
        showCardEditModal(board.title, columnIndex, cardIndex, card.title, card.description, card.assignee, card.dueDate);
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

      // 카드 상세 정보 모달 열기
      cardElement.onclick = function() {
        showCardDetailsModal(card);
      };
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

// 초기화 함수 호출
initialize();

/// 보드 수정 모달 열기 함수
function openEditBoardModal(boardTitle) {
  var modal = document.getElementById('edit-board-modal');
  var board = boards.find(b => b.title === boardTitle);

  if (board) {
    document.getElementById('edit-board-name').value = board.title;
    document.getElementById('edit-board-description').value = board.content;

    // 저장 버튼에 클릭 이벤트 리스너 추가
    var saveButton = document.getElementById('save-board-button');
    saveButton.onclick = function() {
      saveBoardChanges(board);
    };

    // 취소 버튼에 클릭 이벤트 리스너 추가
    var cancelButton = document.getElementById('cancel-edit-button');
    cancelButton.onclick = function() {
      closeModal(modal); // 취소 시 모달 닫기
    };

    // 모달 닫기 버튼에 클릭 이벤트 리스너 추가
    var closeButton = modal.querySelector('.close');
    closeButton.addEventListener('click', function() {
      closeModal(modal);
    });

    // 모달 보이기
    modal.style.display = 'block';
  }
}

// 모달 닫기 함수
function closeModal(modal) {
  modal.style.display = 'none';
}

// saveBoardChanges 함수 정의
function saveBoardChanges(board) {
  var newTitle = document.getElementById('edit-board-name').value;
  var newContent = document.getElementById('edit-board-description').value;

  if (newTitle.trim() === '') {
    alert('보드 이름을 입력하세요.');
    return;
  }

  // 보드 정보 업데이트
  board.title = newTitle;
  board.content = newContent;

  // 사이드바 및 칸반보드 업데이트
  redrawSidebar();
  displayBoard(board);

  // 모달 닫기
  closeModal(document.getElementById('edit-board-modal'));
}

// 컬럼 수정 모달 열기 함수
function showColumnEditModal(boardTitle, columnIndex, currentColumnName) {
  var modal = document.getElementById('edit-column-modal');
  var editColumnNameInput = document.getElementById('edit-column-name');
  var saveColumnButton = document.getElementById('save-column-button');
  var cancelEditColumnButton = document.getElementById('cancel-edit-column-button');

  // 현재 컬럼 이름을 입력 필드에 설정
  editColumnNameInput.value = currentColumnName;

  // 저장 버튼 클릭 시 처리할 이벤트 설정
  saveColumnButton.onclick = function() {
    var newColumnName = editColumnNameInput.value.trim();

    // 유효성 검사: 이름이 비어있지 않아야 함
    if (newColumnName === '') {
      alert('컬럼 이름을 입력하세요.');
      return;
    }

    // 보드 및 컬럼 인덱스, 새로운 컬럼 이름을 전달하여 컬럼 이름 업데이트
    updateColumnName(boardTitle, columnIndex, newColumnName);

    // 모달 닫기
    hideColumnEditModal();
  };

  // 취소 버튼 클릭 시 모달 닫기
  cancelEditColumnButton.onclick = function() {
    hideColumnEditModal();
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

// 모달 창 닫기 함수
function closeModal(modalId) {
  var modal = document.getElementById(modalId);
  modal.style.display = 'none';
}

// 카드 수정 모달 열기 함수
function showCardEditModal(boardTitle, columnIndex, cardIndex, currentTitle, currentDescription, currentAssignee, currentDueDate) {
  // 기존에 열려있는 모달 창 닫기
  closeModal('show-card-modal'); // 예시: 다른 모달의 ID를 여기에 입력

  // 나머지 코드는 이전 답변에서 사용한 것과 동일
  var modal = document.getElementById('edit-card-modal');
  var editCardTitleInput = document.getElementById('edit-card-title');
  var editCardContentTextarea = document.getElementById('edit-card-content');
  var editCardAssigneeInput = document.getElementById('edit-card-assignee');
  var editCardDueDateInput = document.getElementById('edit-card-due-date');

  editCardTitleInput.value = currentTitle;
  editCardContentTextarea.value = currentDescription;
  editCardAssigneeInput.value = currentAssignee;
  editCardDueDateInput.value = currentDueDate;

  var saveEditCardButton = document.getElementById('save-edit-card-button');
  saveEditCardButton.onclick = function() {
    saveCardEdit(boardTitle, columnIndex, cardIndex);
    hideCardEditModal();
  };

  modal.style.display = 'block';
}

// 카드 수정 버튼 클릭 이벤트 리스너 추가
var editCardButton = document.getElementById('edit-card-button'); // 예시: 카드 수정 버튼의 ID를 여기에 입력
editCardButton.onclick = function() {
  // 카드 수정 모달 열기 함수 호출
  showCardEditModal(boardTitle, columnIndex, cardIndex, currentTitle, currentDescription, currentAssignee, currentDueDate);
};


// 카드 수정 저장
function saveCardEdit(boardTitle, columnIndex, cardIndex) {
  var editedTitle = document.getElementById('edit-card-title').value;
  var editedDescription = document.getElementById('edit-card-content').value;
  var editedAssignee = document.getElementById('edit-card-assignee').value;
  var editedDueDate = document.getElementById('edit-card-due-date').value;

  // 실제로 데이터를 업데이트하고 화면을 다시 그리는 작업이 필요합니다.
  var board = findBoard(boardTitle);
  if (board) {
    var cardToUpdate = board.columns[columnIndex].cards[cardIndex];
    cardToUpdate.title = editedTitle;
    cardToUpdate.content = editedDescription;
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