package com.example.tartanhacks2019;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

public class Tab3Fragment extends Fragment {

    private SpotifyAppRemote mSpotifyAppRemote;
    private TextView songName;
    private TextView songArtist;
    private Button pausePlayButton;
    private Boolean isPlaying;
    final ParseUser user = ParseUser.getCurrentUser();

    private String playlist1 = "spotify:playlist:37i9dQZF1DX8ymr6UES7vc";
    private String playlist2 = "spotify:album:4YwxdwsnGkCqmTR9A5t6N9";
    private String playlist3 = "spotify:user:spotify:playlist:37i9dQZF1DWZd79rJ6a7lp";
    private String playlist4 = "spotify:user:spotify:playlist:37i9dQZF1DX0SM0LYsmbMT";
    private String playlist5 = "spotify:user:spotify:playlist:37i9dQZF1DWSkMjlBZAZ07";
    private  String playlist6 = "spotify:user:spotify:playlist:37i9dQZF1DWSiZVO2J6WeI";
    private String currPlaylist = playlist1;

    private ImageView ivPlaylist1;
    private ImageView ivPlaylist2;
    private ImageView ivPlaylist3;
    private ImageView ivPlaylist4;
    private ImageView ivPlaylist5;
    private ImageView ivPlaylist6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("SpotifyFragment", "Here");
        return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("SpotifyFragment", "Here2");
        songName = view.findViewById(R.id.tvTitle);
        songArtist = view.findViewById(R.id.tvArtist);

        ivPlaylist1 = view.findViewById(R.id.ivPlaylist1);
        ivPlaylist1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist1;
                connected();

            }
        });
        ivPlaylist2 = view.findViewById(R.id.ivPlaylist2);
        ivPlaylist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist2;
                connected();
            }
        });
        ivPlaylist3 = view.findViewById(R.id.ivPlaylist3);
        ivPlaylist3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist3;
                connected();
            }
        });
        ivPlaylist4 = view.findViewById(R.id.ivPlaylist4);
        ivPlaylist4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist4;
                connected();
            }
        });
        ivPlaylist5 = view.findViewById(R.id.ivPlaylist5);
        ivPlaylist5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist5;
                connected();
            }
        });
        ivPlaylist6 = view.findViewById(R.id.ivPlaylist6);
        ivPlaylist6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currPlaylist = playlist6;
                connected();
            }
        });
        getLastPlayed();

        isPlaying = false;
        pausePlayButton = view.findViewById(R.id.pausePlay);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SpotifyFragment", "Pressed pause/play button");
                if(isPlaying) {
                    mSpotifyAppRemote.getPlayerApi().pause();
                    isPlaying = false;
                }
                else {
                    mSpotifyAppRemote.getPlayerApi().resume();
                    isPlaying = true;
                    mSpotifyAppRemote.getPlayerApi()
                            .subscribeToPlayerState()
                            .setEventCallback(playerState -> {
                                final Track track = playerState.track;
                                if (track != null) {
                                    Log.d("SpotifyFragment", track.name + " by " + track.artist.name);
                                    songName.setText(track.name);
                                    songArtist.setText(track.artist.name);
                                    user.put("lastSong", track.name);
                                    user.put("lastArtist", track.artist.name);
                                    user.saveInBackground();
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("SpotifyFragment", "onStart");
        ConnectionParams connectionParams =
                new ConnectionParams.Builder("dcaef32df78944cc90fc69348956171f")
                        .setRedirectUri("http://example.com/callback/")
                        .showAuthView(true)
                        .build();
        SpotifyAppRemote.connect(getActivity(), connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("SpotifyFragment", "Connected to Spotify.");
                if(isPlaying == true) connected();
            }
            @Override
            public void onFailure(Throwable throwable) {
                Log.d("SpotifyFragment", throwable.getMessage(), throwable);
            }
        });
    }

    public void connected() {
        mSpotifyAppRemote.getPlayerApi().play(currPlaylist);
        isPlaying = true;
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("SpotifyFragment", track.name + " by " + track.artist.name);
                        songName.setText(track.name);
                        songArtist.setText(track.artist.name);
                        user.put("lastSong", track.name);
                        user.put("lastArtist", track.artist.name);
                        user.saveInBackground();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        Log.d("SpotifyFragment", "Disconnected");
    }

    public void getLastPlayed() {
        String lastSong = user.getString("lastSong");
        String lastArtist = user.getString("lastArtist");
        songName.setText(lastSong);
        songArtist.setText(lastArtist);
    }
}
