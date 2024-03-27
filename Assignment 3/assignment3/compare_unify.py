#!/usr/bin/env python3
import glob

read_files = [
    "../guardian/00/Axel_Springer_threatens_to_take_investment_out_of_Germany.txt",
    "../guardian/00/Budget__conserve_our_culture.txt",
    "../guardian/00/Cath_Kidston_s_florals_are_a_hit_with_Asian_market_as_sales_boom.txt",
    "../guardian/00/Desert_hockey_is_dead__so_will_Coyotes_move_to_Quebec__Seattle_or_Portland_.txt",
    "../guardian/00/How_Detour_is_helping_fans_get_their_favourite_bands_to_town.txt",
    "../guardian/00/How_we_made_kscope__an_image_sharing_app_for_university_applicants.txt",
    "../guardian/00/How_we_made_the_Hansel___Gretel_Newsstand_app_for_iPad.txt",
    "../guardian/00/Jurassic_World_and_the__legacyquel___2015_global_box_office_in_review.txt",
    "../guardian/00/The_chancellor_s_reaction_to_SoftBank_s_takeover_of_ARM_was_ludicrous.txt",
    "../guardian/00/What_women_need_to_do_the_business.txt",
    "../guardian/00/Why_ad_men_like_Adam_Crozier_are_dominating_television.txt",
    "../guardian/00/Why_aren_t_more_slasher_movies_set_in_blocks_of_flats_.txt",
    "../guardian/00/_Saving__art_for_the_nation_merely_disguises_the_poverty_of_our_museums.txt",
    "../guardian/01/15_Caribbean_gems.txt",
    "../guardian/01/Can_PledgeMusic_s_direct_to_fan_approach_unlock_untapped_revenue_.txt",
    "../guardian/01/Leaked_map_reveals_chronic_mercury_epidemic_in_Peru.txt",
    "../guardian/01/SABMiller_does_not_need_a_saviour___it_s_time_for_the_chairman_to_speak_up.txt",
    "../guardian/01/The_digital_readership_is_out_there__But_is_there_money_too_.txt",
    "../guardian/02/A_working_life__the_lingerie_designer.txt",
    "../guardian/02/BBC_should_replace_licence_fee_with_subscription__says_Armando_Iannucci.txt",
    "../guardian/02/Is_M_S_s_pre_cut_avocado_a_convenience_too_far_.txt",
    "../guardian/02/Let_me_entertain_you.txt",
    "../guardian/02/The_Indy_may_find_digital_isn_t_easy_when_you_re_a_paper_born_for_print.txt",
    "../guardian/02/Vampire_hunting_in_New_Orleans.txt",
    "../guardian/04/Barney_Francis___Football_punditry_is_an_absolute_art_form_.txt",
    "../guardian/04/Complementary_medicines_review_calls_for_stricter_labelling_in_Australia.txt",
    "../guardian/04/For_long_copy_to_flourish_again_it_needs_investment_by_the_advertising__industry.txt",
    "../guardian/04/Liverpool_Everyman___the_theatre_was_as_rough_as_our_performances_.txt",
    "../guardian/04/Roma_s_legion_have_the_power_to_topple_Juventus_from_the_throne_of_Italy.txt",
    "../guardian/04/Rosemary_Tonks__the_lost_poet.txt",
    "../guardian/04/Thompson_flies_the_FA_flag_in_Mali.txt",
    "../guardian/04/Writers_pick_the_best_books_of_2014__part_one.txt",
    "../guardian/04/_I_started_to_realise_what_fiction_could_be__And_I_thought__wow__You_can_take__on_the_world_.txt",
    "../guardian/05/Apple_is__arrogant__and_encryption_is__oversold___ex_NSA_lawyer_tells_SXSW.txt",
    "../guardian/05/Apple_v_FBI_congressional_hearing___as_it_happened.txt",
    "../guardian/05/Beyond_email__could_startup_Slack_change_the_way_you_work_.txt",
    "../guardian/05/Italy_referendum_Q_A__the_big_economic_questions_answered.txt",
    "../guardian/05/We_need_to_save_our_cities__The_fate_of_the_Corkman_pub_and_Sirius_is_social__destruction.txt",
    "../guardian/07/Barnaby_Joyce_says__inoperable__Senate_may_trigger_double_dissolution_election.txt",
    "../guardian/07/Coalition_distances_itself_from_previous_bracket_creep_warnings.txt",
    "../guardian/07/Double_dissolution_is_a__live_option__as_Morrison_rules_out__pixie_horse__tax__cuts.txt",
    "../guardian/07/Why_the_super_rich_won_t_live_in_a_house_fit_for_a_king.txt",
    "../guardian/08/Interview_with_a_Bookstore__Prairie_Lights__a_shop_fit_for_presidents.txt",
    "../guardian/08/Take_the_kids_to___Eden_Project__near_St_Austell__Cornwall.txt",
    "../guardian/08/Translation_Tuesday__About_My_Mother_by_Tahar_Ben_Jelloun___extract.txt",
    "../guardian/16/Barbies__chocolate__scotch__give_your_godchildren_what_they_don_t_get_at_home.txt",
    "../guardian/16/Carney_tells_MPs_Brexit_no_longer_biggest_risk_to_stability__WEF_warns_on__inequality___as_it_happened.txt",
    "../guardian/16/Factsheet__Buying_a_home.txt",
    "../guardian/17/Brexit__a_brain_drain_and_Trump_s_tweets___Guardian_Social_as_it_happened.txt",
    "../guardian/17/Factsheet__Buying_a_home.txt",
    "../guardian/17/Italy_referendum_Q_A__the_big_economic_questions_answered.txt",
    "../guardian/17/Polly_Toynbee_meets_Donald_Trump__the_1988_interview.txt",
    "../guardian/18/Can_e_bikes_revolutionise_long_distance_commuting_.txt",
    "../guardian/18/FTSE_up_nearly_2__with_Merlin_higher_but_travel_shares_down_after_Istanbul.txt",
    "../guardian/18/Guns_N__Roses___Rock_n_roll_is_like_an_aphrodisiac_for_people_who_have__everyday_jobs_.txt",
    "../guardian/18/Why_I_was_wrong_on_house_prices_in_2016___and_why_I_might_still_be_proved__right.txt",
    "../guardian/19/A_data_strategy_could_help_independent_retailers_to_thrive.txt",
    "../guardian/19/Juncker_puts_veteran_French_politician_in_charge_of_Brexit_talks.txt",
    "../guardian/19/Lore_of_the_jungle__life_with_Costa_Rica_s_indigenous_peoples.txt",
    "../guardian/19/There_s_a_better_way_for_private_schools_to_help_the_state_sector.txt",
    "../guardian/19/The_heat_is_on_in_Miami.txt",
    "../guardian/19/Twitter_road_trips_USA__Baltimore_to_Bar_Harbor__day_one___live.txt",
    "../guardian/20/Fabio_Liverani_s_Orient_claim_lifeline_with_Port_Vale_win.txt",
    "../guardian/20/Roma_close_on_record_as_Bradley_counters_charge_of_squad_s_lack_of_depth.txt",
    "../guardian/21/Manchester_City_4_0_Borussia_M_nchengladbach__Champions_League___as_it_happened.txt",
    "../guardian/21/The_Observer_quiz_of_the_year_2014.txt",
    "../guardian/22/UK_gamers__more_women_play_games_than_men__report_finds.txt",
    "../guardian/23/Adblocking_is_helping_the_digital_sharks_eat_the_minnows___what_s_the__solution_.txt",
    "../guardian/23/Altered_images__photography_as_a_tool_for_gender_equality.txt",
    "../guardian/23/Barbies__chocolate__scotch__give_your_godchildren_what_they_don_t_get_at_home.txt",
    "../guardian/23/Carney_tells_MPs_Brexit_no_longer_biggest_risk_to_stability__WEF_warns_on__inequality___as_it_happened.txt",
    "../guardian/23/European_stock_markets_fall__but_pound_rallies__as_Trump_fears_bite___as_it__happened.txt",
    "../guardian/23/Grumbling_Green_needs_to_understand_he_s_not_dealing_with_a_legal_process.txt",
    "../guardian/23/Neoliberalism___the_ideology_at_the_root_of_all_our_problems.txt",
    "../guardian/23/Pound_and_shares_rally_after_two_days_of_record_Brexit_losses___as_it_happened.txt",
]

# with open(read_files[0], "r") as infile:
#     print(infile.read())

with open("./compare_unified.txt", "wb") as outfile:
    for f in read_files:
        with open(f, "rb") as infile:
            outfile.write(("--------- BEGINNING NEW FILE " + f + "\n").encode())
            outfile.write(infile.read())