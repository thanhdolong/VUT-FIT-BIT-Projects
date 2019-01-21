/**
 *   Author: Do Long Thanh
 *   Login: xdolon00
 *   File: proj2.c
 *   Date: 2.5.2016    
 *   Description: 
 *       Roller coaster. Prace s procesy.
 * 12/15
**/

#include <fcntl.h>
#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <sys/shm.h>
#include <time.h>
#include <unistd.h>


// Chybove vypisy
enum err_codes_num {
    ERR_ARGS,
    ERR_BAD_ARGS,
    ERR_SEM,
    ERR_SHARED_MEMORY,
    ERR_FILE,
    ERR_FORK,
    TEST
};

const char *err_codes[] = {
    "ERROR - spatny pocet argumentu!",
    "ERROR - neocekavany vstup!",
    "ERROR - nelze vytvorit semafor!",
    "ERROR - nelze vytvorit sdilena pamet!",
    "ERROR - nelze otevrit soubor!",
    "ERROR - nelze provést fork!",
    "NOERROR - funguje to wole",
};


/**
 * @brief      Vypsani chyboveho hlaseni    
 */
void
sys_error(int i)
{
    fprintf(stderr, "%s\n", err_codes[i]);
}

#define WRITEFILE "/xdolon_sem_writefile"
#define MUTEX01 "/xdolon_sem_mutex01"
#define MUTEX02 "/xdolon_sem_mutex02"
#define BOARD "/xdolon_sem_boardqueue"
#define UNBOARD "/xdolon_sem_unboardboardqueue"
#define ALLABOARD "/xdolon_sem_allAboard"
#define ALLASHORE "/xdolon_sem_allAshore"
#define FINALSEM "/xdolon_sem_finalsem"

// deklarace souboru
FILE *file;

// deklarovany semaforu
sem_t *writefile;
sem_t *mutex01;
sem_t *mutex02;
sem_t *board;
sem_t *unboard;
sem_t *allaboard;
sem_t *allashore;
sem_t *finalsem;

// deklarovany sdilene pameti

int counter_id = 0, *counter = NULL;
int boarders_id = 0, *boarders = NULL;
int unboarders_id = 0, *unboarders = NULL;



/**
 * @brief      Inicializace semaforu
 */
int
init_sem()
{

    // Semafor pro zapisovani souboru
    if ((writefile = sem_open(WRITEFILE, O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro pasazera
    if ((mutex01 = sem_open(MUTEX01, O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro pasazera
    if ((mutex02 = sem_open(MUTEX02, O_CREAT | O_EXCL, 0666, 1)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro nastupovani
    if ((board = sem_open(BOARD, O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro vystupovani
    if ((unboard = sem_open(UNBOARD, O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro nastupovani (zaplneno)
    if ((allaboard = sem_open(ALLABOARD, O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro nastupovani (posledni pasazer)
    if ((allashore = sem_open(ALLASHORE, O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }
    // Semafor pro finalizaci pro pasazera a voziku
    if ((finalsem = sem_open(FINALSEM, O_CREAT | O_EXCL, 0666, 0)) == SEM_FAILED) {
    return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}


/**
 * @brief      Inicializace sdilene pameti
 */
int
init_shared_memory()
{
    // pocitadlo (radky)
    counter_id = shmget(IPC_PRIVATE, sizeof(int), IPC_CREAT | 0666);
    if (counter_id == -1) {
    return EXIT_FAILURE;
    }
    counter = (int *) shmat(counter_id, NULL, 0);
    if (counter == NULL) {
    return EXIT_FAILURE;
    }
    // Nastup pasazeru - pocitadlo
    boarders_id = shmget(IPC_PRIVATE, sizeof(int), IPC_CREAT | 0666);
    if (boarders_id == -1) {
    return EXIT_FAILURE;
    }
    boarders = (int *) shmat(boarders_id, NULL, 0);
    if (boarders == NULL) {
    return EXIT_FAILURE;
    }
    // Vystup pasazeru - pocitadlo
    unboarders_id = shmget(IPC_PRIVATE, sizeof(int), IPC_CREAT | 0666);
    if (unboarders_id == -1) {
    return EXIT_FAILURE;
    }
    unboarders = (int *) shmat(unboarders_id, NULL, 0);
    if (unboarders == NULL) {
    return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}


/**
 * @brief      Cisteni semaforu, sdilene pameti a souboru
 */
void
clear_all()
{
    fclose(file);

    // cisteni semaforu 
    sem_close(writefile);
    sem_close(mutex01);
    sem_close(mutex02);
    sem_close(board);
    sem_close(unboard);
    sem_close(allaboard);
    sem_close(allashore);
    sem_close(finalsem);

    sem_unlink(WRITEFILE);
    sem_unlink(MUTEX01);
    sem_unlink(MUTEX02);
    sem_unlink(BOARD);
    sem_unlink(UNBOARD);
    sem_unlink(ALLABOARD);
    sem_unlink(ALLASHORE);
    sem_unlink(FINALSEM);

    // cisteni sdileneho semaforu
    shmdt(counter);
    shmdt(boarders);
    shmdt(unboarders);

    shmctl(counter_id, IPC_RMID, NULL);
    shmctl(boarders_id, IPC_RMID, NULL);
    shmctl(unboarders_id, IPC_RMID, NULL);
}



int
main(int argc, char *argv[])
{
    int P = 0;
    int C = 0;
    int PT = 0;
    int RT = 0;

    // kontrola vstupu
    if (argc > 1) {
    if (argc != 5) {
        sys_error(ERR_ARGS);
        return EXIT_FAILURE;
    }

    char *arg_err = NULL;
    int err_count = 0;

    long int arg1 = strtol(argv[1], &arg_err, 10);
    if (*arg_err) {
        err_count++;
    }
    long int arg2 = strtol(argv[2], &arg_err, 10);
    if (*arg_err) {
        err_count++;
    }
    long int arg3 = strtol(argv[3], &arg_err, 10);
    if (*arg_err) {
        err_count++;
    }
    long int arg4 = strtol(argv[4], &arg_err, 10);
    if (*arg_err) {
        err_count++;
    }

    if ((err_count != 0) ||
        (arg1 <= 0) ||
        (arg2 <= 0 || arg2 >= arg1 || (arg1 % arg2 != 0)) ||
        ((arg3 < 0 || arg3 > 5000) || (arg4 < 0 || arg4 > 5000))) {
        sys_error(ERR_BAD_ARGS);
        return EXIT_FAILURE;
    }

    P = arg1;
    C = arg2;
    PT = arg3;
    RT = arg4;
    }

    // inicializace semaforu
    if (init_sem() == EXIT_FAILURE) {
    clear_all();
    sys_error(ERR_SEM);
    return EXIT_FAILURE;
    }

    // inicializace sdilene pameti
    if (init_shared_memory() == EXIT_FAILURE) {
    clear_all();
    sys_error(ERR_SHARED_MEMORY);
    return EXIT_FAILURE;
    }

    // inicializace souboru
    if ((file = fopen("proj2.out", "w")) == NULL) {
    clear_all();
    sys_error(ERR_FILE);
    return EXIT_FAILURE;
    }

    setbuf(stdout, NULL);
    setbuf(stderr, NULL);
    setbuf(file, NULL);

    /*
     * Hlavni proces
     */



    // vozik
    pid_t fc_car = fork();


    if (fc_car < 0) {
    sys_error(ERR_FORK);
    clear_all();
    return EXIT_FAILURE;
    } else if (fc_car == 0) {
    sem_wait(writefile);
    fprintf(file, "%d\t: C 1\t: started\n", ++*counter);
    sem_post(writefile);

    // cyklus
    int             ride_count = (P / C);
    for (int i = 1; i <= ride_count; i++) {

        sem_wait(writefile);
        fprintf(file, "%d\t: C 1\t: load\n", ++*counter);
        sem_post(writefile);

        for (int j = 1; j <= C; j++) {
        sem_post(board);
        }

        sem_wait(allaboard);

        sem_wait(writefile);
        fprintf(file, "%d\t: C 1\t: run\n", ++*counter);
        sem_post(writefile);

        if (RT > 0)
        usleep(((unsigned int) (rand() % (RT))) * 1000);

        sem_wait(writefile);
        fprintf(file, "%d\t: C 1\t: unload\n", ++*counter);
        sem_post(writefile);

        for (int j = 1; j <= C; j++) {
        sem_post(unboard);
        }

        sem_wait(allashore);

    }           // konec cyklu

    sem_wait(writefile);
    fprintf(file, "%d\t: C 1\t: finished\n", ++*counter);
    sem_post(writefile);
    sem_post(finalsem);

    exit(0);
    }



    // pasazer
    pid_t fc_passengers = fork();
    if (fc_passengers < 0) {
    sys_error(ERR_FORK);
    clear_all();
    return EXIT_FAILURE;
    } else if (fc_passengers == 0) {

    pid_t passengers[P];
    for (int x = 1; x <= P; x++) {
        passengers[x] = fork();
        if (passengers[x] < 0) {
        sys_error(ERR_FORK);
        clear_all();
        return EXIT_FAILURE;
        } else if (passengers[x] == 0) {

        sem_wait(writefile);
        fprintf(file, "%d\t: P %d\t: started\n", ++*counter, x);
        sem_post(writefile);

        sem_wait(board);

        sem_wait(writefile);
        fprintf(file, "%d\t: P %d\t: board\n", ++*counter, x);
        sem_post(writefile);

        sem_wait(mutex01);

        *boarders = *boarders + 1;
        if (*boarders == C) {

            sem_wait(writefile);
            fprintf(file, "%d\t: P %d\t: board last\n", ++*counter, x);
            *boarders = 0;
            sem_post(writefile);

            sem_post(allaboard);
        } else {
            sem_wait(writefile);
            fprintf(file, "%d\t: P %d\t: board order %d\n",
                ++*counter, x, *boarders);
            sem_post(writefile);
        }

        sem_post(mutex01);

        sem_wait(unboard);

        sem_wait(writefile);
        fprintf(file, "%d\t: P %d\t: unboard\n", ++*counter, x);
        sem_post(writefile);

        sem_wait(mutex02);

        *unboarders = *unboarders + 1;
        if (*unboarders == C) {

            sem_wait(writefile);
            fprintf(file, "%d\t: P %d\t: unboard last\n", ++*counter, x);
            *unboarders = 0;
            sem_post(writefile);

            sem_post(allashore);
        } else {
            sem_wait(writefile);
            fprintf(file, "%d\t: P %d\t: unboard order %d\n", ++*counter, x, *unboarders);
            sem_post(writefile);
        }
        sem_post(mutex02);

        sem_wait(finalsem);
        sem_wait(writefile);
        fprintf(file, "%d\t: P %d\t: finished\n", ++*counter, x);
        sem_post(writefile);
        sem_post(finalsem);

        exit(0);
        }

        if (PT > 0)
        usleep(((unsigned int) (rand() % (PT))) * 1000);
    }           // konec for


    exit(0);
    }
    // cisteni clear_all
    clear_all();
    return EXIT_SUCCESS;
}
