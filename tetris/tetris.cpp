#include <iostream>
#include <thread>
#include <vector>
#include <chrono>
#include <ncurses.h>
//#include <Windows.h>

using namespace std;

wstring tetromino[7];
int nFieldWidth = 12;
int nFieldHeight = 18;
unsigned char *pField = nullptr;

int nScreenWidth = 80;
int nScreenHeight = 30;

/* r = 0, 1, 2, 3, for 0, 90, 180, 270 degrees clockwise */
int rotate(int px, int py, int r)
{
  switch (r % 4) {
  case 0: return 4*py + px;
  case 1: return 12 + py - 4*px;
  case 2: return 15 - 4*py - px;
  case 3: return 3 - py + 4*px;
  }
  return 0;
}


//location of piece given by x and y position of top left of 4X4 grid
bool DoesPieceFit(int nTetromino, int nRotation, int nPosX, int nPosY)
{
  for (int px = 0; px < 4; px++) {
    for (int py = 0; py < 4; py++) {
            
      // get index into piece
      int pi = rotate(px, py, nRotation);
            
      // get index into field (y times width plus x)
      int fi = (nPosY + py) * nFieldWidth + (nPosX + px);
            
      // check we're not looking at things out of array bounds
      if (nPosX + px >= 0 && nPosX + px < nFieldWidth) {
	if (nPosY + py >= 0 && nPosY + py < nFieldHeight) {
	  // collision detection
	  if (tetromino[nTetromino][pi] == L'X' && pField[fi] != 0)
	    return false;
	}
      }
    }
  }
  return true;
}


int main()
{
  tetromino[0].append(L"..X.");
  tetromino[0].append(L"..X.");
  tetromino[0].append(L"..X.");
  tetromino[0].append(L"..X.");

  tetromino[1].append(L"..X.");
  tetromino[1].append(L".XX.");
  tetromino[1].append(L".X..");
  tetromino[1].append(L"....");

  tetromino[2].append(L".X..");
  tetromino[2].append(L".XX.");
  tetromino[2].append(L"..X.");
  tetromino[2].append(L"....");

  tetromino[3].append(L"..X.");
  tetromino[3].append(L"..X.");
  tetromino[3].append(L".XX.");
  tetromino[3].append(L"....");

  tetromino[4].append(L".X..");
  tetromino[4].append(L".X..");
  tetromino[4].append(L".XX.");
  tetromino[4].append(L"....");

  tetromino[5].append(L"..X.");
  tetromino[5].append(L".XX.");
  tetromino[5].append(L"..X.");
  tetromino[5].append(L"....");

  tetromino[6].append(L".XX.");
  tetromino[6].append(L".XX.");
  tetromino[6].append(L"....");
  tetromino[6].append(L"....");

  // initialise playing field
  // set everything to zero unless it's on the side or the bottom
  pField = new unsigned char[nFieldWidth*nFieldHeight];
  for (int x = 0; x < nFieldWidth; x++) {
    for (int y = 0; y < nFieldHeight; y++) {
      pField[y*nFieldWidth + x] = (x == 0 || x==nFieldWidth - 1 || y == nFieldHeight - 1) ? 9 : 0;
    }
  }
    
  // setup to use command line as a screen buffer
  wchar_t *screen = new wchar_t[nScreenWidth*nScreenHeight];
  for (int i = 0; i < nScreenWidth * nScreenHeight; i++) {
    screen[i] = L' ';
  }
  //HANDLE hConsole = CreateConsoleScreenBuffer(GENERIC_READ | GENERIC_WRITE, 0, NULL, CONSOLE_TEXTMODE_BUFFER, NULL);
  //SetConsoleActiveScreenBuffer(hConsole);
  //DWORD dwBytesWritten = 0;
    
  bool bGameOver = false;
    
  int nCurrentPiece = 0;
  int nCurrentRotation = 0;
  int nCurrentX = nFieldWidth / 2;    //start at middle
  int nCurrentY = 0;                  // start at top
    
  bool bKey[4];
  bool bRotateHold = false;
    
  int nSpeed = 20;
  int nSpeedCounter = 0; //counts number of game ticks
  bool bForceDown = false;
  int nPieceCount = 0;
  int nScore = 0;
    
  vector<int> vLines;
    
  while (!bGameOver) {
        
    // game timing
    this_thread::sleep_for(std::chrono::milliseconds(50)); // game tick
    nSpeedCounter++;
    bForceDown = (nSpeedCounter == nSpeed);
        
    // user input
    for (int k = 0; k < 4; k++) {
      //virtual key codes for right, left, down, Z
      //     bKey[k] = (0x8000 & GetAsyncKeyState((unsigned char)("\x27\x25\x28Z"[k]))) != 0;
    }
        
    // game logic
    if (bForceDown) {
      if (DoesPieceFit(nCurrentPiece, nCurrentRotation, nCurrentX, nCurrentY + 1)) {
	nCurrentY++; //push piece down if can
      } else {
	// lock current pice in the field
	for (int px = 0; px < 4; px++) {
	  for (int py = 0; py < 4; py++) {
	    if (tetromino[nCurrentPiece][rotate(px, py, nCurrentRotation)] == L'X') {
	      pField[nCurrentY + py * nFieldWidth + (nCurrentX + px)] = nCurrentPiece + 1;
	    }
	  }
	}
	nPieceCount++;
	if (nPieceCount % 10 == 0) {
	  if (nSpeed >= 10) nSpeed--;
	}
                
                
	// check have we made a line
	// only need to check for lines within bounds of current piece
	for (int py = 0; py < 4; py++) {
	  if (nCurrentY + py < nFieldHeight - 1) { // boundary check
	    bool bLine = true; // start by assuming there is a line
	    // check all positions except boundaries and fail if any are empty
	    for (int px = 1; px < nFieldWidth - 1; px++) {
	      bLine &= (pField[(nCurrentY + py) * nFieldWidth + px]) != 0;
	    }
	    if (bLine) {
	      // remove the line and set to ====
	      for (int px = 1; px < nFieldWidth - 1; px++) {
		pField[(nCurrentY + py) * nFieldWidth + px] = 8;
	      }
	      vLines.push_back(nCurrentY + py);
	    }
	  }
	}
                
	nScore += 25; // 25 points for each piece placed
	if (!vLines.empty()) {
	  // one line gives 100 points, 4 lines gives 1600 points
	  nScore += (1 << vLines.size()) * 100;
	}
                
	// chose next piece
	nCurrentX = nFieldWidth / 2;
	nCurrentY = 0;
	nCurrentRotation = 0;
	nCurrentPiece = rand() % 7;
                
	// if piece does not fit
	bGameOver = !DoesPieceFit(nCurrentPiece, nCurrentRotation, nCurrentX, nCurrentY);
      }
            
      nSpeedCounter = 0;
    }
        
    /*        
    // if right key pressed, check if it can fit there, and if so move it
    if (bKey[0]) {
      if (DoesPieceFit(nCurrentPiece, nCurrentRotation, nCurrentX + 1, nCurrentY)) {
	nCurrentX = nCurrentX + 1;
      }
    }
        
    // if left key pressed, check if it can fit there, and if so move it
    if (bKey[1]) {
      if (DoesPieceFit(nCurrentPiece, nCurrentRotation, nCurrentX - 1, nCurrentY)) {
	nCurrentX = nCurrentX - 1;
      }
    }
        
    // if down key pressed, check if it can fit there, and if so move it
    nCurrentY += (bKey[2] && DoesPieceFit(nCurrentPiece, nCurrentRotation, nCurrentX, nCurrentY + 1)) ? 1 : 0;

    // rotation key pressed
    if (bKey[3]) {
      nCurrentRotation += (!bRotateHold && DoesPieceFit(nCurrentPiece, nCurrentRotation + 1, nCurrentX, nCurrentY)) ? 1 : 0;
      bRotateHold = true;
    } else {
      bRotateHold = false;
    }
        
    */  
        
    // render output
        


    /*    
    // Draw field
    for (int x = 0; x < nFieldWidth; x++) {
      for (int y = 0; y < nFieldHeight; y++) {
	screen[(y+2)*nScreenWidth + (x+2)] = L" ABCDEFG=#"[pField[y*nFieldWidth + x]];
      }
    }
        
    // draw current piece
    for (int px = 0; px < 4; px++) {
      for (int py = 0; py < 4; py++) {
	if (tetromino[nCurrentPiece][rotate(px, py, nCurrentRotation)] == L'X') {
	  // position offset by 2 for the boundary and by current X and Y positions
	  screen[(nCurrentY + py + 2)*nScreenWidth + (nCurrentX + px + 2)] = nCurrentPiece + 65;
	}
      }
    }
    */
        
    // display score
    swprintf_s(&screen[2 * nScreenWidth + nFieldWidth + 6], 16, L"SCORE: %8d", nScore);
        
        
    if (!vLines.empty()) {
      // Display Frame
      // cheating a bit by doing game logic in the drawing loop
      //WriteConsoleOutputCharacter(hConsole, screen, nScreenWidth * nScreenHeight, { 0,0 }, &dwBytesWritten);
      this_thread::sleep_for(chrono::milliseconds(400));
            
      // then remove the lines and move down all the ones above them
      for (auto &v : vLines) { // if a line exists a number is returned which is the row
	for (int px = 1; px < nFieldWidth - 1; px++) {
	  for (int py = v; py > 0; py--) {
	    pField[py * nFieldWidth + px] = pField[(py - 1) * nFieldWidth + px];
	    pField[px] = 0;
	  }
	}
      }
    }
        
    // Display frame
    //WriteConsoleOutputCharacter(hConsole, screen, nScreenWidth * nScreenHeight, { 0,0 }, &dwBytesWritten);
  } // exit while loop when game over
    
    //CloseHandle(hConsole); //can't use cout until we let go of screen buffer
  cout << "Game Over!! Score:" << nScore << endl;
  //system("pause");
    
  cout << "hello cpp" << endl;
  return 0;
}
