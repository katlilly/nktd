#include <ncurses.h>

int main()
{

  initscr(); //creates stdscr
  raw(); //can't exit with ctrl-c etc
  //cbreak();
  //attron(A_STANDOUT | A_UNDERLINE); //highlighted and underlined
  // need to look at what features are in xterm, bash, etc
  //mvprintw(12,40, "READ THIS NOW");
  //attroff(A_STANDOUT | A_UNDERLINE);
  if (has_colors) {
    start_color();
    init_pair(1,COLOR_RED, COLOR_BLUE); //foreground, background
    attron(COLOR_PAIR(1));
    printw("aahh my eyes");
    attroff(COLOR_PAIR(1));
  }

  /* int x = 4; */
  /* printw("first words %d", x); */
  /* addch('a'); // writes char to screen */
  /* move(12,13); // moves the curser (y,x) */

  /* mvprintw(15,20,"movement"); */
  /* mvaddch(12, 50, '@'); */

  getch(); //so program doesn't end immediately */
  endwin(); 
  
  
  //getch(); //need to press enter to continue from here
  //endwin(); //sort of a destructor
  


  return 0;
}
