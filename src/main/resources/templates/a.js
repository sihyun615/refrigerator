// 컬럼 전체 조회 함수
function fetchColumns(boardId) {
  const auth = getToken();
  if (!auth) {
    window.location.href = '/users/login-page';
    return;
  }

  $.ajax({
    url: `http://localhost:8080/boards/${boardId}/columns`,
    type: 'GET',
    beforeSend: function(xhr) {
      xhr.setRequestHeader('Authorization', auth);
    },
    success: function(data) {
      var board = boards.find(b => b.boardId === parseInt(boardId, 10));
      if (board) {
        // 새 컬럼 데이터를 기존 컬럼 데이터와 병합
        data.data.forEach(newColumn => {
          console.log(newColumn);
          var existingColumn = board.columns.find(c => c.columnId === newColumn.columnId);
          if (existingColumn) {
            existingColumn.name = newColumn.name;
            existingColumn.cards = newColumn.cards; // 필요한 경우 카드를 덮어씀
          } else {
            board.columns.push(newColumn);
          }
        });
        displayColumns(board);
      }
    },
    error: function() {
      alert('컬럼 조회에 실패했습니다. 다시 시도해주세요.');
    }
  });
}