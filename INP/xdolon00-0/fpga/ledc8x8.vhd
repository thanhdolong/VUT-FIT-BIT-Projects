library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;

entity ledc8x8 is
port ( -- Sem doplnte popis rozhrani obvodu.
	SMCLK, RESET: in std_logic;
	LED, ROW : out std_logic_vector(7 downto 0)
);
end ledc8x8;

architecture main of ledc8x8 is

    -- Sem doplnte definice vnitrnich signalu.
    signal counter: natural range 0 to 255;
    signal led_change: std_logic := '0';
    signal helper: std_logic := '0';
    signal row_sig: std_logic_vector( 7 downto 0) := "00000001";
    signal smclk_pin: natural range  0 to 3686400 := 0;

begin

    -- Sem doplnte popis funkce obvodu (zakladni konstrukce VHDL jako napr.
    -- prirazeni signalu, multiplexory, dekodery, procesy...).
    -- DODRZUJTE ZASADY PSANI SYNTETIZOVATELNEHO VHDL UVEDENE NA WEBU:
    -- http://merlin.fit.vutbr.cz/FITkit/docs/navody/synth_templates.html

    -- Nezapomente take doplnit mapovani signalu rozhrani na piny FPGA
    -- v souboru ledc8x8.ucf.

    -- generovani 1/256 SMCLK pro přepínání mezi řádkami
    smclk_256 : process( SMCLK, RESET )
    begin
    if (RESET = '1')
    then
    	counter <= 0;
	elsif (smclk'event and smclk='1' )
	then
    	-- kontrola 1/256
    	if (counter = 255)
    	then
    		helper <= '1';
    		counter <= 0;
    	else
    		helper <= '0';
    		counter <= counter+1;
    	end if ; 				
        if (smclk_pin = 3686400) then
            led_change <= not led_change;
            smclk_pin <= 0; 
        else
            smclk_pin <= smclk_pin + 1;        
        end if;
    end if ;
    end process ; -- smclk_256

    -- rotator pro vypisovani radki
    row_signal : process( SMCLK )
    begin
    if (smclk'event and smclk='1') then
        if (helper = '1') then
            row_sig <= row_sig(6 downto 0) & row_sig(7);
        end if ;
    end if ;
    end process ; -- row_signal

    my_name : process( row_sig, led_change )
    begin
    if (led_change = '0') then
           case( row_sig ) is  
              when "00000001" => LED <= "11111111";
              when "00000010" => LED <= "10011111";
              when "00000100" => LED <= "10011111";
              when "00001000" => LED <= "10011111";
              when "00010000" => LED <= "10011111";
              when "00100000" => LED <= "10000001";
              when "01000000" => LED <= "10000001";
              when "10000000" => LED <= "11111111";
              when others => LED <= "11111111";
          end case ;       
          else
            case( row_sig ) is  
              when "00000001" => LED <= "11111111";
              when "00000010" => LED <= "10000001";
              when "00000100" => LED <= "10000001";
              when "00001000" => LED <= "11100111";
              when "00010000" => LED <= "11100111";
              when "00100000" => LED <= "11100111";
              when "01000000" => LED <= "11100111";
              when "10000000" => LED <= "11111111";
              when others => LED <= "11111111";
          end case ; 
    end if ;
    end process ; -- my_name

    ROW <= row_sig;
end main;