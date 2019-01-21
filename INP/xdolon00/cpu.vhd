-- cpu.vhd-- cpu.vhd: Simple 8-bit CPU (BrainLove interpreter)
-- Copyright (C) 2016 Brno University of Technology,
--                    Faculty of Information Technology
-- Author(s): xdolon00 - Do Long Thanh
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

-- ----------------------------------------------------------------------------
--                        Entity declaration
-- ----------------------------------------------------------------------------
entity cpu is
 port (
   CLK   : in std_logic;  -- hodinovy signal
   RESET : in std_logic;  -- asynchronni reset procesoru
   EN    : in std_logic;  -- povoleni cinnosti procesoru
 
   -- synchronni pamet ROM
   CODE_ADDR : out std_logic_vector(11 downto 0); -- adresa do pameti
   CODE_DATA : in std_logic_vector(7 downto 0);   -- CODE_DATA <- rom[CODE_ADDR] pokud CODE_EN='1'
   CODE_EN   : out std_logic;                     -- povoleni cinnosti
   
   -- synchronni pamet RAM
   DATA_ADDR  : out std_logic_vector(9 downto 0); -- adresa do pameti
   DATA_WDATA : out std_logic_vector(7 downto 0); -- mem[DATA_ADDR] <- DATA_WDATA pokud DATA_EN='1'
   DATA_RDATA : in std_logic_vector(7 downto 0);  -- DATA_RDATA <- ram[DATA_ADDR] pokud DATA_EN='1'
   DATA_RDWR  : out std_logic;                    -- cteni (1) / zapis (0)
   DATA_EN    : out std_logic;                    -- povoleni cinnosti
   
   -- vstupni port
   IN_DATA   : in std_logic_vector(7 downto 0);   -- IN_DATA <- stav klavesnice pokud IN_VLD='1' a IN_REQ='1'
   IN_VLD    : in std_logic;                      -- data platna
   IN_REQ    : out std_logic;                     -- pozadavek na vstup data
   
   -- vystupni port
   OUT_DATA : out  std_logic_vector(7 downto 0);  -- zapisovana data
   OUT_BUSY : in std_logic;                       -- LCD je zaneprazdnen (1), nelze zapisovat
   OUT_WE   : out std_logic                       -- LCD <- OUT_DATA pokud OUT_WE='1' a OUT_BUSY='0'
 );
end cpu;


-- ----------------------------------------------------------------------------
--                      Architecture declaration
-- ----------------------------------------------------------------------------
architecture behavioral of cpu is
 -- PC
 signal sig_inc_pc:        std_logic := '0';
 signal sig_dec_pc:        std_logic := '0';
 signal std_code_addr:     std_logic_vector(11 downto 0);

 -- CNT
 signal sig_inc_cnt:       std_logic := '0';
 signal sig_dec_cnt:       std_logic := '0';
 signal cnt_result:        std_logic := '0';
 signal cnt:               std_logic_vector(7 downto 0);

 -- TMP
 signal load:              std_logic := '0';
 signal tmp:               std_logic_vector(7 downto 0);
 signal DATA_RDATA_result: std_logic := '0';

 -- PTR
 signal sig_inc_ptr:       std_logic := '0';
 signal sig_dec_ptr:       std_logic := '0';
 signal std_data_addr:     std_logic_vector(9 downto 0);  

 -- Multiplexor
 signal sel :              std_logic_vector(1 downto 0);

 -- Decoder
 type inst_type is (incPtr, decPtr, incVal, decVal, loopStart, loopEnd, printVal, printSave, dollar, excl, endPrc, unknow);
 signal dec_inst: inst_type;

 -- Konecny automat
 type FSMstate is (SInit, SFetch, SDecode, SincPtr, SdecPtr, SincVal, SincVal01, SdecVal, SdecVal01, SloopStart, SloopStart01, SloopStart02, SloopStart03, SloopEnd, SloopEnd01, SloopEnd02, SloopEnd03, SloopEnd04, SprintVal, SprintVal01, SprintSave, Sdollar, Sexcl, SendPrc, Sunknow);
 signal pstate : FSMstate; -- actual state
 signal nstate : FSMstate; -- next state


begin

-- ----------------------------------------------------------------------------
--                      PC
-- ----------------------------------------------------------------------------

 PC : process( CLK,RESET )
 begin
   if (RESET = '1') then
      std_code_addr <= (others => '0');   
   elsif (CLK'event and CLK='1') then
      if (sig_inc_pc = '1') then
         std_code_addr <= std_code_addr + 1;
      elsif (sig_dec_pc = '1') then         
         std_code_addr <= std_code_addr - 1;  
      end if ;  
   end if ;
 end process ; -- PC

-- ----------------------------------------------------------------------------
--                      CNT
-- ----------------------------------------------------------------------------

 CNT_prc : process( CLK,RESET )
 begin
   if (RESET = '1') then
      cnt <= (others => '0');
   elsif (CLK'event and CLK='1') then
      if (sig_inc_cnt = '1') then
         cnt <= cnt + 1 ;
      elsif (sig_dec_cnt = '1') then         
         cnt <= cnt - 1 ;
      end if ;
   end if ;
 end process ; -- CNT_prc

 CNT_comparator : process( cnt )
 begin
   if (cnt = "00000000" ) then
      cnt_result <= '1';
   else
      cnt_result <= '0';
   end if ;
 end process ; -- CNT_comparator

-- ----------------------------------------------------------------------------
--                      TMP
-- ----------------------------------------------------------------------------

 TMP_prc : process( CLK,RESET )
 begin
   if (RESET = '1') then

   elsif (CLK'event and CLK='1') then
      if (load = '1') then
         tmp <= DATA_RDATA;
      end if ;
   end if ;
 end process ; -- TMP_prc

 DATA_RDATA_comparator : process( DATA_RDATA )
 begin
    if (DATA_RDATA = "00000000") then
      DATA_RDATA_result <= '1';
    else
      DATA_RDATA_result <= '0';
    end if ;
 end process ; -- DATA_RDATA_comparator

-- ----------------------------------------------------------------------------
--                      PTR
-- ----------------------------------------------------------------------------

PTR_prc : process( CLK, RESET )
begin
   if (RESET = '1') then
      std_data_addr <= (others => '0');   
   elsif (CLK'event and CLK='1') then
      if (sig_inc_ptr = '1') then
         std_data_addr <= std_data_addr + 1;
      elsif (sig_dec_ptr = '1') then         
         std_data_addr <= std_data_addr - 1; 
      end if ;   
   end if ;
end process ; -- PTR_prc


-- ----------------------------------------------------------------------------
--                      Multiplexor
-- ----------------------------------------------------------------------------

Multiplexor: process(sel, tmp, DATA_RDATA, IN_DATA)
begin
   case sel is
      when "00" => DATA_WDATA <= IN_DATA;
      when "01" => DATA_WDATA <= tmp;
      when "10" => DATA_WDATA <= DATA_RDATA - 1;
      when others => DATA_WDATA <= DATA_RDATA + 1;
   end case;
end process; -- Multiplexor

-- ----------------------------------------------------------------------------
--                      ROM, RAM, I/O
-- ----------------------------------------------------------------------------

CODE_ADDR <= std_code_addr;
DATA_ADDR <= std_data_addr;
OUT_DATA <= DATA_RDATA;

-- ----------------------------------------------------------------------------
--                      Dekocer pro 8.bitoveho instrukce
-- ----------------------------------------------------------------------------

Decoder_prc : process( CODE_DATA )
begin
   case( CODE_DATA ) is
         when X"3E" =>  dec_inst <= incPtr;      --">"
         when X"3C" =>  dec_inst <= decPtr;      --"<"
         when X"2B" =>  dec_inst <= incVal;      --"+"
         when X"2D" =>  dec_inst <= decVal;      --"-"
         when X"5B" =>  dec_inst <= loopStart;   --"["
         when X"5D" =>  dec_inst <= loopEnd;     --"]"
         when X"2E" =>  dec_inst <= printVal;    --"."
         when X"2C" =>  dec_inst <= printSave;   --","
         when X"24" =>  dec_inst <= dollar;      --"$"
         when X"21" =>  dec_inst <= excl;        --"!"
         when X"00" =>  dec_inst <= endPrc;      --"null"
         when others => dec_inst <= unknow;   --jinak je tam hloupost a preskoc
   end case ;
   
end process ; -- Decoder_prc

-- ----------------------------------------------------------------------------
--                      Konecny automat
-- ----------------------------------------------------------------------------

--Present State registr
pstatereg: process(RESET, CLK)
begin
   if (RESET='1') then
      pstate <= SInit;
   elsif (CLK'event) and (CLK='1') then
      if (EN = '1') then
         pstate <= nstate;
      end if;
   end if;
end process; --pstatereg

FSM : process( pstate, dec_inst, OUT_BUSY, IN_VLD, DATA_RDATA_result, cnt_result)
begin

-- INIT  
DATA_RDWR <= '1';
DATA_EN <= '0';
sel <= "00";
sig_inc_ptr <= '0';
sig_dec_ptr <= '0';
sig_inc_pc <= '0';
sig_dec_pc <= '0';
sig_inc_cnt <= '0';
sig_dec_cnt <= '0';
load <= '0';
CODE_EN <= '0';
IN_REQ <= '0';
OUT_WE <= '0'; 

case( pstate ) is

   when SInit =>
   nstate <= SFetch;

   when SFetch =>
   CODE_EN <= '1';
   nstate <= SDecode;

   when SDecode =>
   case( dec_inst ) is
   
         when incPtr =>  nstate <= SincPtr;          --">"
         when decPtr =>  nstate <= SdecPtr;          --"<"
         when incVal =>  nstate <= SincVal;          --"+"
         when decVal =>  nstate <= SdecVal;          --"-"
         when loopStart =>  nstate <= SloopStart;    --"["
         when loopEnd =>  nstate <= SloopEnd;        --"]"
         when printVal =>  nstate <= SprintVal;      --"."
         when printSave =>  nstate <= SprintSave;    --","
         when dollar =>  nstate <= Sdollar;          --"$"
         when excl =>  nstate <= Sexcl;              --"!"
         when endPrc =>  nstate <= SendPrc;          --"null"
         when others => nstate <= Sunknow;           --jinak je tam hloupost a preskoc
   
   end case ;

   -- ">"
   when SincPtr =>
   sig_inc_ptr <= '1';
   sig_inc_pc <= '1';
   nstate <= SFetch;

   -- "<"
   when SdecPtr =>
   sig_dec_ptr <= '1';
   sig_inc_pc <= '1';
   nstate <= SFetch;

   -- "-"
   when SdecVal =>
   DATA_EN <= '1';
   DATA_RDWR <= '1';
   nstate <= SDecVal01;

   when SDecVal01 =>
   DATA_EN <= '1';
   DATA_RDWR <= '0';
   sel <= "10";
   sig_inc_pc <= '1';
   nstate <= SFetch;

   -- "+"
   when SincVal =>
   DATA_EN <= '1';
   DATA_RDWR <= '1';
   nstate <= SincVal01;

   when SincVal01 =>
   DATA_EN <= '1';
   DATA_RDWR <= '0';
   sel <= "11";
   sig_inc_pc <= '1';
   nstate <= SFetch;

   -- "."
   when SprintVal =>
      DATA_EN <= '1';
      DATA_RDWR <= '1';
      nstate <= SprintVal01;

   when SprintVal01 =>
   if ( OUT_BUSY = '1') then
      nstate <= SprintVal01;
   else
      OUT_WE <= '1';
      sig_inc_pc <= '1';
      nstate <= SFetch;
   end if;

   -- ","
   when SprintSave =>
   -- IN REQ ‹ 1
   IN_REQ <= '1';
   --while (!IN VLD) {}
   if (IN_VLD = '1') then
      DATA_EN <= '1';
      DATA_RDWR <= '0';
      sel <= "00";
      sig_inc_pc <= '1';
      nstate <= sFetch;
      else
      nstate <= SprintSave;
   end if ;



   -- "$"
   when Sdollar =>
      DATA_EN <= '1';
      DATA_RDWR <= '1';
      load <= '1';
      sig_inc_pc <= '1';
      nstate <= SFetch;

   -- "!"
   when Sexcl =>
      DATA_EN <= '1';
      DATA_RDWR <= '0';
      sel <= "01";
      sig_inc_pc <= '1';
      nstate <= SFetch;

   -- null
   when SendPrc  =>
    nstate <= SendPrc;


   -- [
   when SloopStart =>
      sig_inc_pc <= '1';
      DATA_EN <= '1';
      DATA_RDWR <= '1';
      nstate <= SloopStart01;

   -- ram[PTR] == 0 
   -- CNT è 1  
   when SloopStart01 =>
      if (DATA_RDATA_result = '1') then
         sig_inc_cnt <= '1';  
         nstate <= SloopStart02;
      else
         nstate <= SFetch;
      end if;


   -- while (CNT != 0)
   when SloopStart02 =>
      if (cnt_result = '1') then
         nstate <= sfetch;  
      else
         CODE_EN <= '1';
         nstate <= SloopStart03;
      end if;

   -- c è rom[PC]
   -- if (c == '[') CNT è CNT + 1 elsif (c == ']') CNT è CNT - 1
   -- PC è PC + 1
   when SloopStart03 =>
      sig_inc_pc <= '1';  

      if (dec_inst = loopStart) then
        sig_inc_cnt <= '1';
      elsif (dec_inst = loopEnd) then
        sig_dec_cnt <= '1';     
      end if;   

      nstate <= SloopStart02;

   -- ]
   when SloopEnd =>

      DATA_EN <= '1';
      DATA_RDWR <= '1';

      nstate <= SloopEnd01; 

   -- if (ram[PTR] == 0)
   -- PC è PC + 1
   -- else
   -- CNT è 1
   -- PC è PC - 1
   when SloopEnd01 =>
      if (DATA_RDATA_result = '1') then
         sig_inc_pc <= '1';  
         nstate <= sFetch;
      else
         sig_dec_pc <= '1';
         sig_inc_cnt <= '1'; 
         nstate <= SloopEnd02;   
      end if ;

   -- while (CNT != 0)
   when SloopEnd02 =>
   if (cnt_result = '1') then
      nstate <= sFetch;  
   else
      CODE_EN <= '1'; 
      nstate <= SloopEnd03;
   end if;
  
  -- c è rom[PC]
  -- if (c == ']') CNT è CNT + 1 elsif (c == '[') CNT è CNT - 1 
   when SloopEnd03 =>
   if (dec_inst = loopEnd) then
     sig_inc_cnt <= '1';
   elsif (dec_inst = loopStart) then
     sig_dec_cnt <= '1';     
   end if;  
   nstate <= SloopEnd04;

   -- if (CNT == 0) PC è PC + 1 else PC è PC - 1
   when SloopEnd04 =>
      if (cnt_result = '1') then
         sig_inc_pc <= '1'; 
      else
         sig_dec_pc <= '1'; 
      end if;

      nstate <= SloopEnd02;

   -- others
   when others =>
    sig_inc_pc <= '1';
    nstate <= SFetch;
    
end case ;

end process ; -- FSM
end behavioral;